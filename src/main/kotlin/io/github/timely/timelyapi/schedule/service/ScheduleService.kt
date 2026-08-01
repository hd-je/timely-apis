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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        validateScheduleValues(
            companySn = companySn,
            title = request.title,
            scheduleType = request.scheduleType,
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
                scheduleType = request.scheduleType.trim(),
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
        scheduleType?.trim()?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_TYPE", it, "Invalid schedule type") }
        status?.trim()?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_STATUS", it, "Invalid schedule status") }

        val page = scheduleRepository.searchActiveSchedules(
            companySn = companySn,
            startDt = startDt,
            endDt = endDt,
            projectSn = projectSn,
            userSn = userSn,
            scheduleType = scheduleType.normalized(),
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
        scheduleType?.trim()?.takeIf { it.isNotBlank() }
            ?.let { validateCommonCode("SCHEDULE_TYPE", it, "Invalid schedule type") }
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
            scheduleType = scheduleType.normalized(),
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
        scheduleSn: Long,
        request: ScheduleDto.UpdateRequest
    ): ScheduleDto.Response {
        validateScheduleValues(
            companySn = companySn,
            title = request.title,
            scheduleType = request.scheduleType,
            status = request.status,
            startDt = request.startDt,
            endDt = request.endDt,
            allDayYn = request.allDayYn,
            ownerUserSn = request.ownerUserSn,
            projectSn = request.projectSn,
            participantUserSns = request.participantUserSns
        )

        val schedule = getActiveSchedule(companySn, scheduleSn)
        schedule.projectSn = request.projectSn
        schedule.ownerUserSn = request.ownerUserSn
        schedule.title = request.title.trim()
        schedule.content = request.content.normalized()
        schedule.scheduleType = request.scheduleType.trim()
        schedule.status = request.status.trim()
        schedule.startDt = request.startDt
        schedule.endDt = request.endDt
        schedule.allDayYn = request.allDayYn.trim()
        schedule.place = request.place.normalized()
        replaceParticipants(companySn, scheduleSn, request.participantUserSns)

        return schedule.toResponse()
    }

    @Transactional
    fun updateScheduleStatus(companySn: Long, scheduleSn: Long, request: ScheduleDto.StatusRequest): ScheduleDto.Response {
        val status = request.status.trim()
        require(status.isNotBlank()) { "Schedule status must not be blank" }
        validateCommonCode("SCHEDULE_STATUS", status, "Invalid schedule status")

        val schedule = getActiveSchedule(companySn, scheduleSn)
        schedule.status = status

        return schedule.toResponse()
    }

    @Transactional
    fun deleteSchedule(companySn: Long, scheduleSn: Long) {
        getActiveSchedule(companySn, scheduleSn).useYn = "N"
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
