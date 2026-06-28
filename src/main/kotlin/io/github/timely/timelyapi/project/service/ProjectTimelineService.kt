package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectTimelineDto
import io.github.timely.timelyapi.project.model.ProjectTimeline
import io.github.timely.timelyapi.project.repository.ProjectTimelineRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ProjectTimelineService(
    private val projectService: ProjectService,
    private val projectTimelineRepository: ProjectTimelineRepository,
    private val commonCodeRepository: CommonCodeRepository
) {

    @Transactional
    fun createTimeline(
        companySn: Long,
        projectSn: Long,
        request: ProjectTimelineDto.CreateRequest
    ): ProjectTimelineDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateTimelineValues(request.phaseNm, request.status, request.startDt, request.endDt)

        val timeline = projectTimelineRepository.save(
            ProjectTimeline(
                projectSn = projectSn,
                phaseNm = request.phaseNm.trim(),
                description = request.description.normalized(),
                status = request.status.trim(),
                startDt = request.startDt,
                endDt = request.endDt,
                sortSeq = request.sortSeq
            )
        )

        return timeline.toResponse()
    }

    @Transactional(readOnly = true)
    fun getTimelines(companySn: Long, projectSn: Long): List<ProjectTimelineDto.Response> {
        projectService.getActiveProject(companySn, projectSn)
        return projectTimelineRepository
            .findByProjectSnAndUseYnOrderBySortSeqAscStartDtAscProjectTimelineSnAsc(projectSn, "Y")
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getTimeline(companySn: Long, projectSn: Long, projectTimelineSn: Long): ProjectTimelineDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        return getActiveTimeline(projectSn, projectTimelineSn).toResponse()
    }

    @Transactional
    fun updateTimeline(
        companySn: Long,
        projectSn: Long,
        projectTimelineSn: Long,
        request: ProjectTimelineDto.UpdateRequest
    ): ProjectTimelineDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateTimelineValues(request.phaseNm, request.status, request.startDt, request.endDt)

        val timeline = getActiveTimeline(projectSn, projectTimelineSn)
        timeline.phaseNm = request.phaseNm.trim()
        timeline.description = request.description.normalized()
        timeline.status = request.status.trim()
        timeline.startDt = request.startDt
        timeline.endDt = request.endDt
        timeline.sortSeq = request.sortSeq

        return timeline.toResponse()
    }

    @Transactional
    fun deleteTimeline(companySn: Long, projectSn: Long, projectTimelineSn: Long) {
        projectService.getActiveProject(companySn, projectSn)
        getActiveTimeline(projectSn, projectTimelineSn).useYn = "N"
    }

    private fun getActiveTimeline(projectSn: Long, projectTimelineSn: Long): ProjectTimeline {
        return projectTimelineRepository.findByProjectTimelineSnAndProjectSnAndUseYn(projectTimelineSn, projectSn, "Y")
            ?: throw IllegalArgumentException("Project timeline not found")
    }

    private fun validateTimelineValues(
        phaseNm: String,
        status: String,
        startDt: LocalDate?,
        endDt: LocalDate?
    ) {
        require(phaseNm.isNotBlank()) { "Timeline phase name must not be blank" }
        require(phaseNm.length <= 200) { "Timeline phase name must be 200 characters or less" }
        require(status.isNotBlank()) { "Timeline status must not be blank" }
        require(startDt == null || endDt == null || !endDt.isBefore(startDt)) {
            "Timeline end date must not be before start date"
        }
        validateCommonCode("PROJECT_TIMELINE_STATUS", status.trim(), "Invalid project timeline status")
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
    }

    private fun statusName(status: String) = codeName("PROJECT_TIMELINE_STATUS", status) ?: status

    private fun codeName(codeGroup: String, code: String): String? {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup, "Y")
            .firstOrNull { it.code == code }
            ?.codeNm
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun ProjectTimeline.toResponse() =
        ProjectTimelineDto.Response(
            projectTimelineSn = projectTimelineSn!!,
            projectSn = projectSn,
            phaseNm = phaseNm,
            description = description,
            status = status,
            statusNm = statusName(status),
            startDt = startDt,
            endDt = endDt,
            sortSeq = sortSeq,
            createDt = createDt,
            updateDt = updateDt
        )
}
