package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.common.PageResponse
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectDto
import io.github.timely.timelyapi.project.model.ProjectAccessRole
import io.github.timely.timelyapi.project.model.ProjectAccessUser
import io.github.timely.timelyapi.project.model.ProjectFile
import io.github.timely.timelyapi.project.model.ProjectMember
import io.github.timely.timelyapi.project.model.Project
import io.github.timely.timelyapi.project.model.ProjectTag
import io.github.timely.timelyapi.project.repository.ProjectAccessRoleRepository
import io.github.timely.timelyapi.project.repository.ProjectAccessUserRepository
import io.github.timely.timelyapi.project.repository.ProjectFileRepository
import io.github.timely.timelyapi.project.repository.ProjectMilestoneRepository
import io.github.timely.timelyapi.project.repository.ProjectMemberRepository
import io.github.timely.timelyapi.project.repository.ProjectRepository
import io.github.timely.timelyapi.project.repository.ProjectTagRepository
import io.github.timely.timelyapi.project.repository.ProjectTaskRepository
import io.github.timely.timelyapi.project.repository.ProjectTimelineRepository
import io.github.timely.timelyapi.project.repository.ProjectUpdateRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectTagRepository: ProjectTagRepository,
    private val projectMemberRepository: ProjectMemberRepository,
    private val projectAccessUserRepository: ProjectAccessUserRepository,
    private val projectAccessRoleRepository: ProjectAccessRoleRepository,
    private val projectFileRepository: ProjectFileRepository,
    private val projectTaskRepository: ProjectTaskRepository,
    private val projectUpdateRepository: ProjectUpdateRepository,
    private val projectMilestoneRepository: ProjectMilestoneRepository,
    private val projectTimelineRepository: ProjectTimelineRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createProject(userSn: Long, companySn: Long, request: ProjectDto.CreateRequest): ProjectDto.Response {
        validateProjectValues(
            companySn = companySn,
            projectNm = request.projectNm,
            ownerUserSn = request.ownerUserSn,
            status = request.status,
            priority = request.priority,
            visibility = request.visibility,
            startDt = request.startDt,
            endDt = request.endDt,
            budgetAmt = request.budgetAmt,
            clientNm = request.clientNm,
            tagNames = request.tagNames,
            memberUserSns = request.memberUserSns,
            accessUserSns = request.accessUserSns,
            accessRoleCodes = request.accessRoleCodes,
            files = request.files
        )

        val project = projectRepository.save(
            Project(
                companySn = companySn,
                projectNm = request.projectNm.trim(),
                description = request.description.normalized(),
                ownerUserSn = request.ownerUserSn,
                status = request.status.trim(),
                priority = request.priority.trim(),
                visibility = request.visibility.trim(),
                progressRate = 0,
                startDt = request.startDt,
                endDt = request.endDt,
                budgetAmt = request.budgetAmt,
                clientNm = request.clientNm.normalized()
            )
        )
        val projectSn = project.projectSn!!
        replaceTags(projectSn, request.tagNames)
        replaceMembers(projectSn, request.ownerUserSn, request.memberUserSns)
        replaceAccessUsers(projectSn, request.accessUserSns)
        replaceAccessRoles(projectSn, request.accessRoleCodes)
        replaceFiles(projectSn, userSn, request.files)

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
    fun updateProject(userSn: Long, companySn: Long, projectSn: Long, request: ProjectDto.UpdateRequest): ProjectDto.Response {
        validateProjectValues(
            companySn = companySn,
            projectNm = request.projectNm,
            ownerUserSn = request.ownerUserSn,
            status = request.status,
            priority = request.priority,
            visibility = request.visibility,
            startDt = request.startDt,
            endDt = request.endDt,
            budgetAmt = request.budgetAmt,
            clientNm = request.clientNm,
            tagNames = request.tagNames,
            memberUserSns = request.memberUserSns,
            accessUserSns = request.accessUserSns,
            accessRoleCodes = request.accessRoleCodes,
            files = request.files
        )

        val project = getActiveProject(companySn, projectSn)
        project.projectNm = request.projectNm.trim()
        project.description = request.description.normalized()
        project.ownerUserSn = request.ownerUserSn
        project.status = request.status.trim()
        project.priority = request.priority.trim()
        project.visibility = request.visibility.trim()
        project.progressRate = calculateProgressRate(projectSn)
        project.startDt = request.startDt
        project.endDt = request.endDt
        project.budgetAmt = request.budgetAmt
        project.clientNm = request.clientNm.normalized()
        val activeProjectSn = project.projectSn!!
        replaceTags(activeProjectSn, request.tagNames)
        replaceMembers(activeProjectSn, request.ownerUserSn, request.memberUserSns)
        replaceAccessUsers(activeProjectSn, request.accessUserSns)
        replaceAccessRoles(activeProjectSn, request.accessRoleCodes)
        replaceFiles(activeProjectSn, userSn, request.files)

        return project.toResponse()
    }

    @Transactional
    fun deleteProject(companySn: Long, projectSn: Long) {
        val project = getActiveProject(companySn, projectSn)
        project.useYn = "N"
        val activeProjectSn = project.projectSn!!
        projectTagRepository.findByProjectSnAndUseYnOrderByProjectTagSnAsc(activeProjectSn, "Y")
            .forEach { it.useYn = "N" }
        projectMemberRepository.findByProjectSnAndUseYnOrderByProjectMemberSnAsc(activeProjectSn, "Y")
            .forEach { it.useYn = "N" }
        projectAccessUserRepository.findByProjectSnAndUseYnOrderByProjectAccessUserSnAsc(activeProjectSn, "Y")
            .forEach { it.useYn = "N" }
        projectAccessRoleRepository.findByProjectSnAndUseYnOrderByProjectAccessRoleSnAsc(activeProjectSn, "Y")
            .forEach { it.useYn = "N" }
        projectFileRepository.findByProjectSnAndUseYnOrderByProjectFileSnAsc(activeProjectSn, "Y")
            .forEach { it.useYn = "N" }
    }

    internal fun recalculateProgressRate(companySn: Long, projectSn: Long): Int {
        val project = getActiveProject(companySn, projectSn)
        val progressRate = calculateProgressRate(project.projectSn!!)
        project.progressRate = progressRate
        return progressRate
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
        priority: String,
        visibility: String,
        startDt: LocalDate?,
        endDt: LocalDate?,
        budgetAmt: BigDecimal?,
        clientNm: String?,
        tagNames: List<String>,
        memberUserSns: List<Long>,
        accessUserSns: List<Long>,
        accessRoleCodes: List<String>,
        files: List<ProjectDto.FileRequest>
    ) {
        require(projectNm.isNotBlank()) { "Project name must not be blank" }
        require(status.isNotBlank()) { "Status must not be blank" }
        require(priority.isNotBlank()) { "Priority must not be blank" }
        require(visibility.isNotBlank()) { "Visibility must not be blank" }
        require(startDt == null || endDt == null || !endDt.isBefore(startDt)) { "End date must not be before start date" }
        require(budgetAmt == null || budgetAmt.signum() >= 0) { "Budget amount must be zero or greater" }
        require(clientNm == null || clientNm.length <= 200) { "Client name must be 200 characters or less" }
        require(userRepository.findByUserSnAndCompanySnAndUseYn(ownerUserSn, companySn, "Y") != null) {
            "Project owner not found"
        }
        validateProjectStatus(status.trim())
        validateCommonCode("PROJECT_PRIORITY", priority.trim(), "Invalid project priority")
        validateCommonCode("PROJECT_VISIBILITY", visibility.trim(), "Invalid project visibility")
        normalizeTagNames(tagNames)
        validateUserSns(companySn, memberUserSns, "Project member not found")
        validateUserSns(companySn, accessUserSns, "Project access user not found")
        normalizeAccessRoleCodes(accessRoleCodes)
        validateFiles(files)
    }

    private fun validateProjectStatus(status: String) {
        validateCommonCode("PROJECT_STATUS", status.trim(), "Invalid project status")
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
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

    private fun replaceMembers(projectSn: Long, ownerUserSn: Long, memberUserSns: List<Long>) {
        projectMemberRepository.findByProjectSnAndUseYnOrderByProjectMemberSnAsc(projectSn, "Y")
            .forEach { it.useYn = "N" }

        upsertMember(projectSn, ownerUserSn, "OWNER")
        memberUserSns
            .filter { it != ownerUserSn }
            .distinct()
            .forEach { upsertMember(projectSn, it, "WORKER") }
    }

    private fun upsertMember(projectSn: Long, userSn: Long, memberRole: String) {
        val member = projectMemberRepository.findByProjectSnAndUserSn(projectSn, userSn)
            ?: ProjectMember(projectSn = projectSn, userSn = userSn, memberRole = memberRole)
        member.memberRole = memberRole
        member.useYn = "Y"
        projectMemberRepository.save(member)
    }

    private fun replaceAccessUsers(projectSn: Long, accessUserSns: List<Long>) {
        projectAccessUserRepository.findByProjectSnAndUseYnOrderByProjectAccessUserSnAsc(projectSn, "Y")
            .forEach { it.useYn = "N" }

        accessUserSns.distinct().forEach { userSn ->
            val accessUser = projectAccessUserRepository.findByProjectSnAndUserSn(projectSn, userSn)
                ?: ProjectAccessUser(projectSn = projectSn, userSn = userSn)
            accessUser.useYn = "Y"
            projectAccessUserRepository.save(accessUser)
        }
    }

    private fun replaceAccessRoles(projectSn: Long, accessRoleCodes: List<String>) {
        projectAccessRoleRepository.findByProjectSnAndUseYnOrderByProjectAccessRoleSnAsc(projectSn, "Y")
            .forEach { it.useYn = "N" }

        normalizeAccessRoleCodes(accessRoleCodes).forEach { authorityCd ->
            val accessRole = projectAccessRoleRepository.findByProjectSnAndAuthorityCd(projectSn, authorityCd)
                ?: ProjectAccessRole(projectSn = projectSn, authorityCd = authorityCd)
            accessRole.useYn = "Y"
            projectAccessRoleRepository.save(accessRole)
        }
    }

    private fun replaceFiles(projectSn: Long, uploadUserSn: Long, files: List<ProjectDto.FileRequest>) {
        projectFileRepository.findByProjectSnAndUseYnOrderByProjectFileSnAsc(projectSn, "Y")
            .forEach { it.useYn = "N" }

        files.forEach { file ->
            projectFileRepository.save(
                ProjectFile(
                    projectSn = projectSn,
                    uploadUserSn = uploadUserSn,
                    originalFileNm = file.originalFileNm.trim(),
                    storedFilePath = file.storedFilePath.trim(),
                    fileSize = file.fileSize,
                    contentType = file.contentType.normalized()
                )
            )
        }
    }

    private fun validateUserSns(companySn: Long, userSns: List<Long>, message: String) {
        userSns.distinct().forEach { userSn ->
            require(userRepository.findByUserSnAndCompanySnAndUseYn(userSn, companySn, "Y") != null) { message }
        }
    }

    private fun normalizeAccessRoleCodes(accessRoleCodes: List<String>): List<String> {
        return accessRoleCodes
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .also { require(it.all { code -> code.length <= 50 }) { "Access role code must be 50 characters or less" } }
    }

    private fun validateFiles(files: List<ProjectDto.FileRequest>) {
        files.forEach {
            require(it.originalFileNm.isNotBlank()) { "Original file name must not be blank" }
            require(it.originalFileNm.length <= 255) { "Original file name must be 255 characters or less" }
            require(it.storedFilePath.isNotBlank()) { "Stored file path must not be blank" }
            require(it.storedFilePath.length <= 500) { "Stored file path must be 500 characters or less" }
            require(it.fileSize == null || it.fileSize >= 0) { "File size must be zero or greater" }
            require(it.contentType == null || it.contentType.length <= 100) { "Content type must be 100 characters or less" }
        }
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun Project.toSimpleResponse() =
        ProjectDto.SimpleResponse(
            projectSn = projectSn!!,
            projectNm = projectNm,
            description = description,
            status = status,
            priority = priority,
            visibility = visibility,
            progressRate = progressRate,
            ownerUserSn = ownerUserSn,
            ownerUserNm = userRepository.findById(ownerUserSn).orElse(null)?.userNm,
            startDt = startDt,
            endDt = endDt,
            budgetAmt = budgetAmt,
            clientNm = clientNm,
            tags = activeTagResponses(),
            memberCount = projectMemberRepository.findByProjectSnAndUseYnOrderByProjectMemberSnAsc(projectSn!!, "Y").size,
            accessExceptionCount = activeAccessExceptionCount(),
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
            priority = priority,
            visibility = visibility,
            progressRate = progressRate,
            startDt = startDt,
            endDt = endDt,
            budgetAmt = budgetAmt,
            clientNm = clientNm,
            tags = activeTagResponses(),
            members = activeMemberResponses(),
            accessUsers = activeAccessUserResponses(),
            accessRoles = activeAccessRoleResponses(),
            files = activeFileResponses(),
            summary = activeDetailSummary(),
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

    private fun Project.activeMemberResponses() =
        projectMemberRepository.findByProjectSnAndUseYnOrderByProjectMemberSnAsc(projectSn!!, "Y")
            .map {
                ProjectDto.MemberResponse(
                    projectMemberSn = it.projectMemberSn!!,
                    userSn = it.userSn,
                    userNm = userRepository.findById(it.userSn).orElse(null)?.userNm,
                    memberRole = it.memberRole
                )
            }

    private fun Project.activeAccessUserResponses() =
        projectAccessUserRepository.findByProjectSnAndUseYnOrderByProjectAccessUserSnAsc(projectSn!!, "Y")
            .map {
                ProjectDto.AccessUserResponse(
                    projectAccessUserSn = it.projectAccessUserSn!!,
                    userSn = it.userSn,
                    userNm = userRepository.findById(it.userSn).orElse(null)?.userNm
                )
            }

    private fun Project.activeAccessRoleResponses() =
        projectAccessRoleRepository.findByProjectSnAndUseYnOrderByProjectAccessRoleSnAsc(projectSn!!, "Y")
            .map {
                ProjectDto.AccessRoleResponse(
                    projectAccessRoleSn = it.projectAccessRoleSn!!,
                    authorityCd = it.authorityCd
                )
            }

    private fun Project.activeFileResponses() =
        projectFileRepository.findByProjectSnAndUseYnOrderByProjectFileSnAsc(projectSn!!, "Y")
            .map {
                ProjectDto.FileResponse(
                    projectFileSn = it.projectFileSn!!,
                    uploadUserSn = it.uploadUserSn,
                    uploadUserNm = userRepository.findById(it.uploadUserSn).orElse(null)?.userNm,
                    originalFileNm = it.originalFileNm,
                    storedFilePath = it.storedFilePath,
                    fileSize = it.fileSize,
                    contentType = it.contentType,
                    createDt = it.createDt
                )
            }

    private fun Project.activeAccessExceptionCount() =
        projectAccessUserRepository.countByProjectSnAndUseYn(projectSn!!, "Y") +
            projectAccessRoleRepository.countByProjectSnAndUseYn(projectSn!!, "Y")

    private fun Project.activeDetailSummary(): ProjectDto.DetailSummaryResponse {
        val activeProjectSn = projectSn!!
        val tasks = projectTaskRepository.findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectTaskSnAsc(activeProjectSn, "Y")
        return ProjectDto.DetailSummaryResponse(
            task = ProjectDto.TaskSummaryResponse(
                totalCount = tasks.size.toLong(),
                completedCount = tasks.count { it.status.isDoneStatus() }.toLong(),
                inProgressCount = tasks.count { it.status == "IN_PROGRESS" }.toLong(),
                pendingCount = tasks.count { it.status == "PENDING" }.toLong()
            ),
            update = ProjectDto.UpdateSummaryResponse(
                totalCount = projectUpdateRepository.countByProjectSnAndUseYn(activeProjectSn, "Y"),
                taskChangeCount = projectUpdateRepository.countByProjectSnAndUpdateTypeAndUseYn(
                    activeProjectSn,
                    "TASK_CHANGE",
                    "Y"
                ),
                riskCount = projectUpdateRepository.countByProjectSnAndUpdateTypeAndUseYn(activeProjectSn, "RISK", "Y")
            ),
            milestone = ProjectDto.MilestoneSummaryResponse(
                totalCount = projectMilestoneRepository.countByProjectSnAndUseYn(activeProjectSn, "Y"),
                completedCount = projectMilestoneRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "COMPLETED", "Y"),
                inProgressCount = projectMilestoneRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "IN_PROGRESS", "Y"),
                plannedCount = projectMilestoneRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "PLANNED", "Y")
            ),
            timeline = ProjectDto.TimelineSummaryResponse(
                totalCount = projectTimelineRepository.countByProjectSnAndUseYn(activeProjectSn, "Y"),
                completedCount = projectTimelineRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "COMPLETED", "Y"),
                inProgressCount = projectTimelineRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "IN_PROGRESS", "Y"),
                plannedCount = projectTimelineRepository.countByProjectSnAndStatusAndUseYn(activeProjectSn, "PLANNED", "Y")
            )
        )
    }

    private fun calculateProgressRate(projectSn: Long): Int {
        val tasks = projectTaskRepository.findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectTaskSnAsc(projectSn, "Y")
        if (tasks.isEmpty()) {
            return 0
        }

        return kotlin.math.round(tasks.sumOf { it.status.progressRate() }.toDouble() / tasks.size).toInt()
            .coerceIn(0, 100)
    }

    private fun String.progressRate(): Int {
        return when (this) {
            "IN_PROGRESS" -> 30
            "REVIEW" -> 70
            "DONE", "COMPLETED" -> 100
            else -> 0
        }
    }

    private fun String.isDoneStatus() = this == "DONE" || this == "COMPLETED"
}
