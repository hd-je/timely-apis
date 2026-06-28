package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectUpdateDto
import io.github.timely.timelyapi.project.model.ProjectUpdate
import io.github.timely.timelyapi.project.model.ProjectUpdateComment
import io.github.timely.timelyapi.project.repository.ProjectTaskRepository
import io.github.timely.timelyapi.project.repository.ProjectUpdateCommentRepository
import io.github.timely.timelyapi.project.repository.ProjectUpdateRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectUpdateService(
    private val projectService: ProjectService,
    private val projectTaskRepository: ProjectTaskRepository,
    private val projectUpdateRepository: ProjectUpdateRepository,
    private val projectUpdateCommentRepository: ProjectUpdateCommentRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createUpdate(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        request: ProjectUpdateDto.CreateRequest
    ): ProjectUpdateDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateUpdateValues(projectSn, request.projectTaskSn, request.updateType, request.title)

        val update = projectUpdateRepository.save(
            ProjectUpdate(
                projectSn = projectSn,
                authorUserSn = userSn,
                projectTaskSn = request.projectTaskSn,
                updateType = request.updateType.trim(),
                title = request.title.trim(),
                content = request.content.normalized(),
                autoYn = "N"
            )
        )

        return update.toResponse()
    }

    @Transactional(readOnly = true)
    fun getUpdates(companySn: Long, projectSn: Long): List<ProjectUpdateDto.SummaryResponse> {
        projectService.getActiveProject(companySn, projectSn)
        return projectUpdateRepository.findByProjectSnAndUseYnOrderByCreateDtDescProjectUpdateSnDesc(projectSn, "Y")
            .map { it.toSummaryResponse() }
    }

    @Transactional(readOnly = true)
    fun getUpdate(companySn: Long, projectSn: Long, projectUpdateSn: Long): ProjectUpdateDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        return getActiveUpdate(projectSn, projectUpdateSn).toResponse()
    }

    @Transactional
    fun updateUpdate(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long,
        request: ProjectUpdateDto.UpdateRequest
    ): ProjectUpdateDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateUpdateValues(projectSn, request.projectTaskSn, request.updateType, request.title)

        val update = getActiveUpdate(projectSn, projectUpdateSn)
        validateAuthor(update.authorUserSn, userSn, "Only the update author can modify this project update")

        update.projectTaskSn = request.projectTaskSn
        update.updateType = request.updateType.trim()
        update.title = request.title.trim()
        update.content = request.content.normalized()

        return update.toResponse()
    }

    @Transactional
    fun deleteUpdate(userSn: Long, companySn: Long, projectSn: Long, projectUpdateSn: Long) {
        projectService.getActiveProject(companySn, projectSn)
        val update = getActiveUpdate(projectSn, projectUpdateSn)
        validateAuthor(update.authorUserSn, userSn, "Only the update author can delete this project update")
        update.useYn = "N"
        projectUpdateCommentRepository.findByProjectUpdateSnAndUseYnOrderByProjectUpdateCommentSnAsc(projectUpdateSn, "Y")
            .forEach { it.useYn = "N" }
    }

    @Transactional
    fun createComment(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long,
        request: ProjectUpdateDto.CommentCreateRequest
    ): ProjectUpdateDto.CommentResponse {
        projectService.getActiveProject(companySn, projectSn)
        getActiveUpdate(projectSn, projectUpdateSn)
        validateCommentContent(request.content)

        return projectUpdateCommentRepository.save(
            ProjectUpdateComment(
                projectUpdateSn = projectUpdateSn,
                authorUserSn = userSn,
                content = request.content.trim()
            )
        ).toResponse()
    }

    @Transactional(readOnly = true)
    fun getComments(
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long
    ): List<ProjectUpdateDto.CommentResponse> {
        projectService.getActiveProject(companySn, projectSn)
        getActiveUpdate(projectSn, projectUpdateSn)
        return projectUpdateCommentRepository.findByProjectUpdateSnAndUseYnOrderByProjectUpdateCommentSnAsc(
            projectUpdateSn,
            "Y"
        ).map { it.toResponse() }
    }

    @Transactional
    fun updateComment(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long,
        projectUpdateCommentSn: Long,
        request: ProjectUpdateDto.CommentUpdateRequest
    ): ProjectUpdateDto.CommentResponse {
        projectService.getActiveProject(companySn, projectSn)
        getActiveUpdate(projectSn, projectUpdateSn)
        validateCommentContent(request.content)

        val comment = getActiveComment(projectUpdateSn, projectUpdateCommentSn)
        validateAuthor(comment.authorUserSn, userSn, "Only the comment author can modify this project update comment")
        comment.content = request.content.trim()

        return comment.toResponse()
    }

    @Transactional
    fun deleteComment(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long,
        projectUpdateCommentSn: Long
    ) {
        projectService.getActiveProject(companySn, projectSn)
        getActiveUpdate(projectSn, projectUpdateSn)
        val comment = getActiveComment(projectUpdateSn, projectUpdateCommentSn)
        validateAuthor(comment.authorUserSn, userSn, "Only the comment author can delete this project update comment")
        comment.useYn = "N"
    }

    private fun getActiveUpdate(projectSn: Long, projectUpdateSn: Long): ProjectUpdate {
        return projectUpdateRepository.findByProjectUpdateSnAndProjectSnAndUseYn(projectUpdateSn, projectSn, "Y")
            ?: throw IllegalArgumentException("Project update not found")
    }

    private fun getActiveComment(projectUpdateSn: Long, projectUpdateCommentSn: Long): ProjectUpdateComment {
        return projectUpdateCommentRepository.findByProjectUpdateCommentSnAndProjectUpdateSnAndUseYn(
            projectUpdateCommentSn,
            projectUpdateSn,
            "Y"
        ) ?: throw IllegalArgumentException("Project update comment not found")
    }

    private fun validateUpdateValues(projectSn: Long, projectTaskSn: Long?, updateType: String, title: String) {
        require(updateType.isNotBlank()) { "Update type must not be blank" }
        require(title.isNotBlank()) { "Update title must not be blank" }
        require(title.length <= 200) { "Update title must be 200 characters or less" }
        validateCommonCode("PROJECT_UPDATE_TYPE", updateType.trim(), "Invalid project update type")
        if (projectTaskSn != null) {
            require(projectTaskRepository.findByProjectTaskSnAndProjectSnAndUseYn(projectTaskSn, projectSn, "Y") != null) {
                "Project task not found"
            }
        }
    }

    private fun validateCommentContent(content: String) {
        require(content.isNotBlank()) { "Comment content must not be blank" }
    }

    private fun validateAuthor(authorUserSn: Long, userSn: Long, message: String) {
        require(authorUserSn == userSn) { message }
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
    }

    private fun updateTypeName(updateType: String): String? {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc("PROJECT_UPDATE_TYPE", "Y")
            .firstOrNull { it.code == updateType }
            ?.codeNm
    }

    private fun taskName(projectTaskSn: Long?): String? {
        return projectTaskSn?.let { projectTaskRepository.findById(it).orElse(null)?.taskNm }
    }

    private fun userName(userSn: Long): String? {
        return userRepository.findById(userSn).orElse(null)?.userNm
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun ProjectUpdate.toSummaryResponse(): ProjectUpdateDto.SummaryResponse {
        val updateSn = projectUpdateSn!!
        return ProjectUpdateDto.SummaryResponse(
            projectUpdateSn = updateSn,
            projectSn = projectSn,
            authorUserSn = authorUserSn,
            authorUserNm = userName(authorUserSn),
            projectTaskSn = projectTaskSn,
            projectTaskNm = taskName(projectTaskSn),
            updateType = updateType,
            updateTypeNm = updateTypeName(updateType),
            title = title,
            content = content,
            autoYn = autoYn,
            commentCount = projectUpdateCommentRepository.countByProjectUpdateSnAndUseYn(updateSn, "Y"),
            createDt = createDt,
            updateDt = updateDt
        )
    }

    private fun ProjectUpdate.toResponse(): ProjectUpdateDto.Response {
        val updateSn = projectUpdateSn!!
        return ProjectUpdateDto.Response(
            projectUpdateSn = updateSn,
            projectSn = projectSn,
            authorUserSn = authorUserSn,
            authorUserNm = userName(authorUserSn),
            projectTaskSn = projectTaskSn,
            projectTaskNm = taskName(projectTaskSn),
            updateType = updateType,
            updateTypeNm = updateTypeName(updateType),
            title = title,
            content = content,
            autoYn = autoYn,
            comments = projectUpdateCommentRepository.findByProjectUpdateSnAndUseYnOrderByProjectUpdateCommentSnAsc(
                updateSn,
                "Y"
            ).map { it.toResponse() },
            createDt = createDt,
            updateDt = updateDt
        )
    }

    private fun ProjectUpdateComment.toResponse() =
        ProjectUpdateDto.CommentResponse(
            projectUpdateCommentSn = projectUpdateCommentSn!!,
            projectUpdateSn = projectUpdateSn,
            authorUserSn = authorUserSn,
            authorUserNm = userName(authorUserSn),
            content = content,
            createDt = createDt,
            updateDt = updateDt
        )
}
