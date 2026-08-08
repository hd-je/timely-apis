package io.github.timely.timelyapi.schedule.service

import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.department.repository.DepartmentRepository
import io.github.timely.timelyapi.project.repository.ProjectRepository
import io.github.timely.timelyapi.schedule.dto.ScheduleDto
import io.github.timely.timelyapi.schedule.model.Schedule
import io.github.timely.timelyapi.schedule.model.ScheduleParticipant
import io.github.timely.timelyapi.schedule.repository.ScheduleParticipantRepository
import io.github.timely.timelyapi.schedule.repository.ScheduleRepository
import io.github.timely.timelyapi.user.model.TimelyUser
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Service
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val projectRepository: ProjectRepository,
    private val departmentRepository: DepartmentRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createSchedule(
        userSn: Long,
        companySn: Long,
        request: ScheduleDto.CreateRequest
    ): ScheduleDto.Response {
        val ownerUserSn = request.ownerUserSn ?: userSn
        if (ownerUserSn != userSn) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Schedule owner must be the authenticated user")
        }
        val scheduleType = normalizeScheduleType(request.scheduleType)
        validateScheduleValues(
            companySn = companySn,
            title = request.title,
            scheduleType = scheduleType,
            status = request.status,
            startDt = request.startDt,
            endDt = request.endDt,
            allDayYn = request.allDayYn,
            ownerUserSn = ownerUserSn,
            projectSn = request.projectSn,
            participantUserSns = request.participantUserSns
        )

        val schedule = scheduleRepository.save(
            Schedule(
                companySn = companySn,
                projectSn = request.projectSn,
                ownerUserSn = ownerUserSn,
                title = request.title.trim(),
                content = request.content.normalized(),
                scheduleType = scheduleType,
                status = request.status.trim(),
                startDt = request.startDt,
                endDt = request.endDt,
                allDayYn = request.allDayYn.trim(),
                place = request.place.normalized()
            )
        )
        replaceParticipants(companySn, schedule.scheduleSn!!, request.participantUserSns)

        return schedule.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchSchedules(
        companySn: Long,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        projectSn: Long?,
        userSn: Long?,
        scheduleType: String?,
        status: String?,
        pageable: Pageable
    ): PageResponse<ScheduleDto.Response> {
        require(!startDt.isAfter(endDt)) { "Start date-time must be before or equal to end date-time" }
        if (projectSn != null) validateActiveProject(companySn, projectSn)
        if (userSn != null) validateActiveUser(companySn, userSn, "Schedule user not found")
        val normalizedScheduleType = scheduleType?.let(::normalizeScheduleType)
        normalizedScheduleType?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_TYPE", it, "Invalid schedule type") }
        status?.trim()?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_STATUS", it, "Invalid schedule status") }

        val page = scheduleRepository.searchActiveSchedules(
            companySn = companySn,
            startDt = startDt,
            endDt = endDt,
            projectSn = projectSn,
            userSn = userSn,
            scheduleType = normalizedScheduleType,
            status = status.normalized(),
            pageable = pageable
        )
        val participantsByScheduleSn = participantsByScheduleSn(page.content.mapNotNull { it.scheduleSn })

        return PageResponse.from(page.map { it.toResponse(participantsByScheduleSn[it.scheduleSn].orEmpty()) })
    }

    @Transactional(readOnly = true)
    fun searchMySchedules(
        companySn: Long,
        userSn: Long,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        projectSn: Long?,
        scheduleType: String?,
        status: String?,
        pageable: Pageable
    ): PageResponse<ScheduleDto.Response> {
        return searchSchedules(
            companySn = companySn,
            startDt = startDt,
            endDt = endDt,
            projectSn = projectSn,
            userSn = userSn,
            scheduleType = scheduleType,
            status = status,
            pageable = pageable
        )
    }

    @Transactional(readOnly = true)
    fun searchUpcomingSchedules(companySn: Long, userSn: Long): List<ScheduleDto.Response> {
        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val from = today.atStartOfDay()
        val to = today.plusDays(7).atTime(LocalTime.MAX)
        return searchMySchedules(
            companySn = companySn,
            userSn = userSn,
            startDt = from,
            endDt = to,
            projectSn = null,
            scheduleType = null,
            status = null,
            pageable = Pageable.unpaged(Sort.by(Sort.Direction.ASC, "startDt"))
        ).content
    }

    @Transactional(readOnly = true)
    fun searchUpcomingTeamSchedules(companySn: Long, userSn: Long): List<ScheduleDto.Response> {
        val today = LocalDate.now(SEOUL_ZONE_ID)
        return searchTeamSchedules(
            companySn = companySn,
            userSn = userSn,
            deptSn = null,
            startDt = today.atStartOfDay(),
            endDt = today.plusDays(7).atTime(LocalTime.MAX),
            projectSn = null,
            scheduleType = null,
            status = null,
            pageable = Pageable.unpaged(Sort.by(Sort.Direction.ASC, "startDt"))
        ).content
    }

    @Transactional(readOnly = true)
    fun searchTeamSchedules(
        companySn: Long,
        userSn: Long,
        deptSn: Long?,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        projectSn: Long?,
        scheduleType: String?,
        status: String?,
        pageable: Pageable
    ): PageResponse<ScheduleDto.Response> {
        require(!startDt.isAfter(endDt)) { "Start date-time must be before or equal to end date-time" }
        if (projectSn != null) validateActiveProject(companySn, projectSn)
        val normalizedScheduleType = scheduleType?.let(::normalizeScheduleType)
        normalizedScheduleType?.takeIf { it.isNotBlank() }
            ?.let {
                validateCommonCode("SCHEDULE_TYPE", it, "Invalid schedule type")
                require(it in TEAM_SCHEDULE_TYPES) {
                    "Team schedules only support ANNUAL_LEAVE and BUSINESS_TRIP types"
                }
            }
        status?.trim()?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_STATUS", it, "Invalid schedule status") }

        val targetDeptSn = deptSn ?: validateActiveUser(companySn, userSn, "Schedule user not found").deptSn
        validateActiveDepartment(companySn, targetDeptSn)

        val page = scheduleRepository.searchActiveTeamSchedules(
            companySn = companySn,
            deptSn = targetDeptSn,
            startDt = startDt,
            endDt = endDt,
            projectSn = projectSn,
            scheduleTypes = normalizedScheduleType?.let(::listOf) ?: TEAM_SCHEDULE_TYPES,
            status = status.normalized(),
            pageable = pageable
        )
        val participantsByScheduleSn = participantsByScheduleSn(page.content.mapNotNull { it.scheduleSn })

        return PageResponse.from(page.map { it.toResponse(participantsByScheduleSn[it.scheduleSn].orEmpty()) })
    }

    @Transactional(readOnly = true)
    fun getSchedule(companySn: Long, scheduleSn: Long): ScheduleDto.Response {
        return getActiveSchedule(companySn, scheduleSn).toResponse()
    }

    @Transactional
    fun updateSchedule(
        companySn: Long,
        userSn: Long,
        scheduleSn: Long,
        request: ScheduleDto.UpdateRequest
    ): ScheduleDto.Response {
        val schedule = getOwnedSchedule(companySn, userSn, scheduleSn)
        if (request.ownerUserSn != schedule.ownerUserSn) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Schedule owner cannot be changed")
        }
        val scheduleType = normalizeScheduleType(request.scheduleType)
        validateScheduleValues(
            companySn = companySn,
            title = request.title,
            scheduleType = scheduleType,
            status = request.status,
            startDt = request.startDt,
            endDt = request.endDt,
            allDayYn = request.allDayYn,
            ownerUserSn = request.ownerUserSn,
            projectSn = request.projectSn,
            participantUserSns = request.participantUserSns
        )

        schedule.projectSn = request.projectSn
        schedule.title = request.title.trim()
        schedule.content = request.content.normalized()
        schedule.scheduleType = scheduleType
        schedule.status = request.status.trim()
        schedule.startDt = request.startDt
        schedule.endDt = request.endDt
        schedule.allDayYn = request.allDayYn.trim()
        schedule.place = request.place.normalized()
        replaceParticipants(companySn, scheduleSn, request.participantUserSns)

        return schedule.toResponse()
    }

    @Transactional
    fun updateScheduleStatus(
        companySn: Long,
        userSn: Long,
        scheduleSn: Long,
        request: ScheduleDto.StatusRequest
    ): ScheduleDto.Response {
        val schedule = getOwnedSchedule(companySn, userSn, scheduleSn)
        val status = request.status.trim()
        require(status.isNotBlank()) { "Schedule status must not be blank" }
        validateCommonCode("SCHEDULE_STATUS", status, "Invalid schedule status")

        schedule.status = status

        return schedule.toResponse()
    }

    @Transactional
    fun deleteSchedule(companySn: Long, userSn: Long, scheduleSn: Long) {
        getOwnedSchedule(companySn, userSn, scheduleSn).useYn = "N"
        scheduleParticipantRepository.findByScheduleSnAndUseYnOrderByScheduleParticipantSnAsc(scheduleSn, "Y")
            .forEach { it.useYn = "N" }
    }

    private fun getActiveSchedule(companySn: Long, scheduleSn: Long): Schedule {
        return scheduleRepository.findByScheduleSnAndCompanySnAndUseYn(scheduleSn, companySn, "Y")
            ?: throw IllegalArgumentException("Schedule not found")
    }

    private fun validateScheduleValues(
        companySn: Long,
        title: String,
        scheduleType: String,
        status: String,
        startDt: LocalDateTime,
        endDt: LocalDateTime,
        allDayYn: String,
        ownerUserSn: Long,
        projectSn: Long?,
        participantUserSns: List<Long>
    ) {
        require(title.isNotBlank()) { "Schedule title must not be blank" }
        require(title.length <= 200) { "Schedule title must be 200 characters or less" }
        require(!startDt.isAfter(endDt)) { "Start date-time must be before or equal to end date-time" }
        require(allDayYn.trim() in setOf("Y", "N")) { "All day flag must be Y or N" }
        require(scheduleType.isNotBlank()) { "Schedule type must not be blank" }
        require(status.isNotBlank()) { "Schedule status must not be blank" }
        validateCommonCode("SCHEDULE_TYPE", scheduleType.trim(), "Invalid schedule type")
        validateCommonCode("SCHEDULE_STATUS", status.trim(), "Invalid schedule status")
        validateActiveUser(companySn, ownerUserSn, "Schedule owner not found")
        if (projectSn != null) validateActiveProject(companySn, projectSn)
        participantUserSns.distinct().forEach {
            validateActiveUser(companySn, it, "Schedule participant not found")
        }
    }

    private fun validateActiveProject(companySn: Long, projectSn: Long) {
        require(projectRepository.findByProjectSnAndCompanySnAndUseYn(projectSn, companySn, "Y") != null) {
            "Schedule project not found"
        }
    }

    private fun validateActiveUser(companySn: Long, userSn: Long, message: String): TimelyUser {
        val user = userRepository.findByUserSnAndCompanySnAndUseYn(userSn, companySn, "Y")
            ?: throw IllegalArgumentException(message)
        require(user.userStatus == "ACTIVE") { message }
        return user
    }

    private fun validateActiveDepartment(companySn: Long, deptSn: Long) {
        require(departmentRepository.findByDeptSnAndCompanySnAndUseYn(deptSn, companySn, "Y") != null) {
            "Schedule department not found"
        }
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
    }

    private fun getOwnedSchedule(companySn: Long, userSn: Long, scheduleSn: Long): Schedule {
        val schedule = getActiveSchedule(companySn, scheduleSn)
        if (schedule.ownerUserSn != userSn) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Only the schedule owner can modify this schedule")
        }
        return schedule
    }

    private fun normalizeScheduleType(scheduleType: String): String {
        val normalized = scheduleType.trim()
        return if (normalized.equals("WORK", ignoreCase = true)) "TASK" else normalized
    }

    private fun replaceParticipants(companySn: Long, scheduleSn: Long, participantUserSns: List<Long>) {
        val existingParticipants = scheduleParticipantRepository.findByScheduleSnOrderByScheduleParticipantSnAsc(scheduleSn)
        existingParticipants.forEach { it.useYn = "N" }

        val participants = participantUserSns.distinct().map { userSn ->
            existingParticipants.firstOrNull { it.userSn == userSn }?.also {
                it.useYn = "Y"
            } ?: ScheduleParticipant(companySn = companySn, scheduleSn = scheduleSn, userSn = userSn)
        }
        scheduleParticipantRepository.saveAll(participants)
    }

    private fun participantsByScheduleSn(scheduleSns: List<Long>): Map<Long, List<ScheduleParticipant>> {
        if (scheduleSns.isEmpty()) return emptyMap()
        return scheduleParticipantRepository.findByScheduleSnInAndUseYnOrderByScheduleParticipantSnAsc(scheduleSns, "Y")
            .groupBy { it.scheduleSn }
    }

    private fun scheduleTypeName(scheduleType: String) = codeName("SCHEDULE_TYPE", scheduleType) ?: scheduleType

    private fun statusName(status: String) = codeName("SCHEDULE_STATUS", status) ?: status

    private fun codeName(codeGroup: String, code: String): String? {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup, "Y")
            .firstOrNull { it.code == code }
            ?.codeNm
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    companion object {
        private val SEOUL_ZONE_ID: ZoneId = ZoneId.of("Asia/Seoul")
        private val TEAM_SCHEDULE_TYPES = listOf("ANNUAL_LEAVE", "BUSINESS_TRIP")
    }

    private fun Schedule.toResponse(
        participants: List<ScheduleParticipant> = scheduleParticipantRepository
            .findByScheduleSnAndUseYnOrderByScheduleParticipantSnAsc(scheduleSn!!, "Y")
    ) = ScheduleDto.Response(
        scheduleSn = scheduleSn!!,
        companySn = companySn,
        projectSn = projectSn,
        projectNm = projectSn?.let { projectRepository.findById(it).orElse(null)?.projectNm },
        ownerUserSn = ownerUserSn,
        ownerUserNm = userRepository.findById(ownerUserSn).orElse(null)?.userNm,
        title = title,
        content = content,
        scheduleType = scheduleType,
        scheduleTypeNm = scheduleTypeName(scheduleType),
        status = status,
        statusNm = statusName(status),
        startDt = startDt,
        endDt = endDt,
        allDayYn = allDayYn,
        place = place,
        participants = participants.map {
            val user = userRepository.findById(it.userSn).orElse(null)
            ScheduleDto.ParticipantResponse(
                userSn = it.userSn,
                userNm = user?.userNm
            )
        },
        createDt = createDt,
        updateDt = updateDt
    )
}
