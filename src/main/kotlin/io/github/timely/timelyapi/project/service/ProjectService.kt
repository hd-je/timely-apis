package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectDto
import io.github.timely.timelyapi.project.model.Project
import io.github.timely.timelyapi.project.model.ProjectTag
import io.github.timely.timelyapi.project.repository.ProjectRepository
import io.github.timely.timelyapi.project.repository.ProjectTagRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectTagRepository: ProjectTagRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createProject(companySn: Long, request: ProjectDto.CreateRequest): ProjectDto.Response {
        validateProjectValues(
            companySn = companySn,
            projectNm = request.projectNm,
            ownerUserSn = request.ownerUserSn,
            status = request.status,
            progressRate = request.progressRate,
            startDt = request.startDt,
            endDt = request.endDt,
            tagNames = request.tagNames
        )

        val project = projectRepository.save(
            Project(
                companySn = companySn,
                projectNm = request.projectNm.trim(),
                description = request.description.normalized(),
                ownerUserSn = request.ownerUserSn,
                status = request.status.trim(),
                progressRate = request.progressRate,
                startDt = request.startDt,
                endDt = request.endDt
            )
        )
        replaceTags(project.projectSn!!, request.tagNames)

        return project.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchProjects(
        companySn: Long,
        status: String?,
        keyword: String?,
        pageable: Pageable
    ): PageResponse<ProjectDto.SimpleResponse> {
        val normalizedStatus = status.normalized()
        if (normalizedStatus != null) {
            validateProjectStatus(normalizedStatus)
        }

        val page = projectRepository.searchActiveProjects(
            companySn = companySn,
            status = normalizedStatus,
            keyword = keyword.normalized(),
            pageable = pageable
        ).map { it.toSimpleResponse() }

        return PageResponse.from(page)
    }

    @Transactional(readOnly = true)
    fun getStatusCounts(companySn: Long): ProjectDto.StatusCountsResponse {
        return ProjectDto.StatusCountsResponse(
            totalCount = projectRepository.countByCompanySnAndUseYn(companySn, "Y"),
            inProgressCount = projectRepository.countByCompanySnAndStatusAndUseYn(companySn, "IN_PROGRESS", "Y"),
            completedCount = projectRepository.countByCompanySnAndStatusAndUseYn(companySn, "COMPLETED", "Y"),
            onHoldCount = projectRepository.countByCompanySnAndStatusAndUseYn(companySn, "ON_HOLD", "Y")
        )
    }

    @Transactional(readOnly = true)
    fun getProject(companySn: Long, projectSn: Long): ProjectDto.Response {
        return getActiveProject(companySn, projectSn).toResponse()
    }

    @Transactional
    fun updateProject(companySn: Long, projectSn: Long, request: ProjectDto.UpdateRequest): ProjectDto.Response {
        validateProjectValues(
            companySn = companySn,
            projectNm = request.projectNm,
            ownerUserSn = request.ownerUserSn,
            status = request.status,
            progressRate = request.progressRate,
            startDt = request.startDt,
            endDt = request.endDt,
            tagNames = request.tagNames
        )

        val project = getActiveProject(companySn, projectSn)
        project.projectNm = request.projectNm.trim()
        project.description = request.description.normalized()
        project.ownerUserSn = request.ownerUserSn
        project.status = request.status.trim()
        project.progressRate = request.progressRate
        project.startDt = request.startDt
        project.endDt = request.endDt
        replaceTags(project.projectSn!!, request.tagNames)

        return project.toResponse()
    }

    @Transactional
    fun deleteProject(companySn: Long, projectSn: Long) {
        val project = getActiveProject(companySn, projectSn)
        project.useYn = "N"
        projectTagRepository.findByProjectSnAndUseYnOrderByProjectTagSnAsc(project.projectSn!!, "Y")
            .forEach { it.useYn = "N" }
    }

    internal fun getActiveProject(companySn: Long, projectSn: Long): Project {
        return projectRepository.findByProjectSnAndCompanySnAndUseYn(projectSn, companySn, "Y")
            ?: throw IllegalArgumentException("Project not found")
    }

    private fun validateProjectValues(
        companySn: Long,
        projectNm: String,
        ownerUserSn: Long,
        status: String,
        progressRate: Int,
        startDt: LocalDate?,
        endDt: LocalDate?,
        tagNames: List<String>
    ) {
        require(projectNm.isNotBlank()) { "Project name must not be blank" }
        require(status.isNotBlank()) { "Status must not be blank" }
        require(progressRate in 0..100) { "Progress rate must be between 0 and 100" }
        require(startDt == null || endDt == null || !endDt.isBefore(startDt)) { "End date must not be before start date" }
        require(userRepository.findByUserSnAndCompanySnAndUseYn(ownerUserSn, companySn, "Y") != null) {
            "Project owner not found"
        }
        validateProjectStatus(status.trim())
        normalizeTagNames(tagNames)
    }

    private fun validateProjectStatus(status: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn("PROJECT_STATUS", status.trim(), "Y")) {
            "Invalid project status"
        }
    }

    private fun replaceTags(projectSn: Long, tagNames: List<String>) {
        val normalizedTagNames = normalizeTagNames(tagNames)
        val currentTags = projectTagRepository.findByProjectSnAndUseYnOrderByProjectTagSnAsc(projectSn, "Y")
        currentTags.forEach { it.useYn = "N" }

        normalizedTagNames.forEach { tagNm ->
            val tag = projectTagRepository.findByProjectSnAndTagNm(projectSn, tagNm)
                ?: ProjectTag(projectSn = projectSn, tagNm = tagNm)
            tag.useYn = "Y"
            projectTagRepository.save(tag)
        }
    }

    private fun normalizeTagNames(tagNames: List<String>): List<String> {
        return tagNames
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .also { require(it.all { tagNm -> tagNm.length <= 100 }) { "Tag name must be 100 characters or less" } }
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun Project.toSimpleResponse() =
        ProjectDto.SimpleResponse(
            projectSn = projectSn!!,
            projectNm = projectNm,
            status = status,
            progressRate = progressRate,
            ownerUserSn = ownerUserSn,
            ownerUserNm = userRepository.findById(ownerUserSn).orElse(null)?.userNm,
            startDt = startDt,
            endDt = endDt,
            tags = activeTagResponses(),
            createDt = createDt,
            updateDt = updateDt
        )

    private fun Project.toResponse() =
        ProjectDto.Response(
            projectSn = projectSn!!,
            projectNm = projectNm,
            description = description,
            ownerUserSn = ownerUserSn,
            ownerUserNm = userRepository.findById(ownerUserSn).orElse(null)?.userNm,
            status = status,
            progressRate = progressRate,
            startDt = startDt,
            endDt = endDt,
            tags = activeTagResponses(),
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )

    private fun Project.activeTagResponses() =
        projectTagRepository.findByProjectSnAndUseYnOrderByProjectTagSnAsc(projectSn!!, "Y")
            .map {
                ProjectDto.TagResponse(
                    projectTagSn = it.projectTagSn!!,
                    tagNm = it.tagNm
                )
            }
}
