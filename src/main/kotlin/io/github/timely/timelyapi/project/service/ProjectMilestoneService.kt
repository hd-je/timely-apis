package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectMilestoneDto
import io.github.timely.timelyapi.project.model.ProjectMilestone
import io.github.timely.timelyapi.project.repository.ProjectMilestoneRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectMilestoneService(
    private val projectService: ProjectService,
    private val projectMilestoneRepository: ProjectMilestoneRepository,
    private val commonCodeRepository: CommonCodeRepository
) {

    @Transactional
    fun createMilestone(
        companySn: Long,
        projectSn: Long,
        request: ProjectMilestoneDto.CreateRequest
    ): ProjectMilestoneDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateMilestoneValues(request.milestoneNm, request.status)

        val milestone = projectMilestoneRepository.save(
            ProjectMilestone(
                projectSn = projectSn,
                milestoneNm = request.milestoneNm.trim(),
                description = request.description.normalized(),
                status = request.status.trim(),
                dueDt = request.dueDt,
                sortSeq = request.sortSeq
            )
        )

        return milestone.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMilestones(companySn: Long, projectSn: Long): ProjectMilestoneDto.ListResponse {
        projectService.getActiveProject(companySn, projectSn)
        val milestones = projectMilestoneRepository
            .findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectMilestoneSnAsc(projectSn, "Y")

        return ProjectMilestoneDto.ListResponse(
            totalCount = milestones.size.toLong(),
            milestones = milestones.map { it.toResponse() }
        )
    }

    @Transactional(readOnly = true)
    fun getMilestone(companySn: Long, projectSn: Long, projectMilestoneSn: Long): ProjectMilestoneDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        return getActiveMilestone(projectSn, projectMilestoneSn).toResponse()
    }

    @Transactional
    fun updateMilestone(
        companySn: Long,
        projectSn: Long,
        projectMilestoneSn: Long,
        request: ProjectMilestoneDto.UpdateRequest
    ): ProjectMilestoneDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateMilestoneValues(request.milestoneNm, request.status)

        val milestone = getActiveMilestone(projectSn, projectMilestoneSn)
        milestone.milestoneNm = request.milestoneNm.trim()
        milestone.description = request.description.normalized()
        milestone.status = request.status.trim()
        milestone.dueDt = request.dueDt
        milestone.sortSeq = request.sortSeq

        return milestone.toResponse()
    }

    @Transactional
    fun deleteMilestone(companySn: Long, projectSn: Long, projectMilestoneSn: Long) {
        projectService.getActiveProject(companySn, projectSn)
        getActiveMilestone(projectSn, projectMilestoneSn).useYn = "N"
    }

    private fun getActiveMilestone(projectSn: Long, projectMilestoneSn: Long): ProjectMilestone {
        return projectMilestoneRepository.findByProjectMilestoneSnAndProjectSnAndUseYn(
            projectMilestoneSn,
            projectSn,
            "Y"
        ) ?: throw IllegalArgumentException("Project milestone not found")
    }

    private fun validateMilestoneValues(milestoneNm: String, status: String) {
        require(milestoneNm.isNotBlank()) { "Milestone name must not be blank" }
        require(milestoneNm.length <= 200) { "Milestone name must be 200 characters or less" }
        require(status.isNotBlank()) { "Milestone status must not be blank" }
        validateCommonCode("PROJECT_MILESTONE_STATUS", status.trim(), "Invalid project milestone status")
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
    }

    private fun statusName(status: String): String? {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(
            "PROJECT_MILESTONE_STATUS",
            "Y"
        ).firstOrNull { it.code == status }?.codeNm
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun ProjectMilestone.toResponse() =
        ProjectMilestoneDto.Response(
            projectMilestoneSn = projectMilestoneSn!!,
            projectSn = projectSn,
            milestoneNm = milestoneNm,
            description = description,
            status = status,
            statusNm = statusName(status),
            dueDt = dueDt,
            sortSeq = sortSeq,
            createDt = createDt,
            updateDt = updateDt
        )
}
