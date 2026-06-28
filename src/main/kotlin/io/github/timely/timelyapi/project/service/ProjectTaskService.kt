package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import io.github.timely.timelyapi.project.dto.ProjectTaskDto
import io.github.timely.timelyapi.project.model.ProjectTask
import io.github.timely.timelyapi.project.model.ProjectUpdate
import io.github.timely.timelyapi.project.repository.ProjectTaskRepository
import io.github.timely.timelyapi.project.repository.ProjectUpdateRepository
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProjectTaskService(
    private val projectService: ProjectService,
    private val projectTaskRepository: ProjectTaskRepository,
    private val projectUpdateRepository: ProjectUpdateRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createTask(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        request: ProjectTaskDto.CreateRequest
    ): ProjectTaskDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateTaskValues(companySn, request.taskNm, request.assigneeUserSn, request.status, request.priority)

        val task = projectTaskRepository.save(
            ProjectTask(
                projectSn = projectSn,
                taskNm = request.taskNm.trim(),
                description = request.description.normalized(),
                assigneeUserSn = request.assigneeUserSn,
                status = request.status.trim(),
                priority = request.priority.trim(),
                sortSeq = request.sortSeq,
                dueDt = request.dueDt,
                completeDt = completeDtFor(request.status.trim())
            )
        )
        recordTaskChangeUpdate(userSn, projectSn, task, "작업 생성")

        return task.toResponse()
    }

    @Transactional(readOnly = true)
    fun getTasks(companySn: Long, projectSn: Long): ProjectTaskDto.ListResponse {
        projectService.getActiveProject(companySn, projectSn)
        val tasks = projectTaskRepository.findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectTaskSnAsc(projectSn, "Y")

        return ProjectTaskDto.ListResponse(
            totalCount = tasks.size.toLong(),
            completedCount = tasks.count { it.status == "COMPLETED" }.toLong(),
            inProgressCount = tasks.count { it.status == "IN_PROGRESS" }.toLong(),
            pendingCount = tasks.count { it.status == "PENDING" }.toLong(),
            tasks = tasks.map { it.toResponse() }
        )
    }

    @Transactional(readOnly = true)
    fun getTask(companySn: Long, projectSn: Long, projectTaskSn: Long): ProjectTaskDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        return getActiveTask(projectSn, projectTaskSn).toResponse()
    }

    @Transactional
    fun updateTask(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectTaskSn: Long,
        request: ProjectTaskDto.UpdateRequest
    ): ProjectTaskDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateTaskValues(companySn, request.taskNm, request.assigneeUserSn, request.status, request.priority)

        val task = getActiveTask(projectSn, projectTaskSn)
        val oldStatus = task.status

        task.taskNm = request.taskNm.trim()
        task.description = request.description.normalized()
        task.assigneeUserSn = request.assigneeUserSn
        task.status = request.status.trim()
        task.priority = request.priority.trim()
        task.sortSeq = request.sortSeq
        task.dueDt = request.dueDt
        task.completeDt = completeDtFor(request.status.trim(), task.completeDt)

        if (oldStatus != task.status) {
            recordTaskChangeUpdate(userSn, projectSn, task, statusChangeTitle(task.status))
        }

        return task.toResponse()
    }

    @Transactional
    fun updateTaskStatus(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectTaskSn: Long,
        request: ProjectTaskDto.StatusRequest
    ): ProjectTaskDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        validateCommonCode("PROJECT_TASK_STATUS", request.status.trim(), "Invalid project task status")

        val task = getActiveTask(projectSn, projectTaskSn)
        val oldStatus = task.status
        task.status = request.status.trim()
        task.completeDt = completeDtFor(task.status, task.completeDt)

        if (oldStatus != task.status) {
            recordTaskChangeUpdate(userSn, projectSn, task, statusChangeTitle(task.status))
        }

        return task.toResponse()
    }

    @Transactional
    fun deleteTask(companySn: Long, projectSn: Long, projectTaskSn: Long) {
        projectService.getActiveProject(companySn, projectSn)
        getActiveTask(projectSn, projectTaskSn).useYn = "N"
    }

    private fun getActiveTask(projectSn: Long, projectTaskSn: Long): ProjectTask {
        return projectTaskRepository.findByProjectTaskSnAndProjectSnAndUseYn(projectTaskSn, projectSn, "Y")
            ?: throw IllegalArgumentException("Project task not found")
    }

    private fun validateTaskValues(
        companySn: Long,
        taskNm: String,
        assigneeUserSn: Long?,
        status: String,
        priority: String
    ) {
        require(taskNm.isNotBlank()) { "Task name must not be blank" }
        require(taskNm.length <= 200) { "Task name must be 200 characters or less" }
        require(status.isNotBlank()) { "Task status must not be blank" }
        require(priority.isNotBlank()) { "Task priority must not be blank" }
        validateCommonCode("PROJECT_TASK_STATUS", status.trim(), "Invalid project task status")
        validateCommonCode("PROJECT_PRIORITY", priority.trim(), "Invalid project task priority")
        if (assigneeUserSn != null) {
            require(userRepository.findByUserSnAndCompanySnAndUseYn(assigneeUserSn, companySn, "Y") != null) {
                "Project task assignee not found"
            }
        }
    }

    private fun validateCommonCode(codeGroup: String, code: String, message: String) {
        require(commonCodeRepository.existsByCodeGroupAndCodeAndUseYn(codeGroup, code, "Y")) { message }
    }

    private fun recordTaskChangeUpdate(userSn: Long, projectSn: Long, task: ProjectTask, titlePrefix: String) {
        projectUpdateRepository.save(
            ProjectUpdate(
                projectSn = projectSn,
                authorUserSn = userSn,
                projectTaskSn = task.projectTaskSn,
                updateType = "TASK_CHANGE",
                title = "$titlePrefix: ${task.taskNm}",
                content = "\"${task.taskNm}\" 작업을 ${statusName(task.status)} 상태로 변경했습니다.",
                autoYn = "Y"
            )
        )
    }

    private fun statusChangeTitle(status: String): String {
        return when (status) {
            "COMPLETED" -> "작업 완료"
            "IN_PROGRESS" -> "작업 시작"
            else -> "작업 상태 변경"
        }
    }

    private fun completeDtFor(status: String, currentCompleteDt: LocalDateTime? = null): LocalDateTime? {
        return if (status == "COMPLETED") currentCompleteDt ?: LocalDateTime.now() else null
    }

    private fun statusName(status: String) = codeName("PROJECT_TASK_STATUS", status) ?: status

    private fun priorityName(priority: String) = codeName("PROJECT_PRIORITY", priority) ?: priority

    private fun codeName(codeGroup: String, code: String): String? {
        return commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup, "Y")
            .firstOrNull { it.code == code }
            ?.codeNm
    }

    private fun String?.normalized() = this?.trim()?.takeIf { it.isNotBlank() }

    private fun ProjectTask.toResponse() =
        ProjectTaskDto.Response(
            projectTaskSn = projectTaskSn!!,
            projectSn = projectSn,
            taskNm = taskNm,
            description = description,
            assigneeUserSn = assigneeUserSn,
            assigneeUserNm = assigneeUserSn?.let { userRepository.findById(it).orElse(null)?.userNm },
            status = status,
            statusNm = statusName(status),
            priority = priority,
            priorityNm = priorityName(priority),
            sortSeq = sortSeq,
            dueDt = dueDt,
            completeDt = completeDt,
            createDt = createDt,
            updateDt = updateDt
        )
}
