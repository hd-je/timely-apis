package io.github.timely.timelyapi.project.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.project.dto.ProjectTaskDto
import io.github.timely.timelyapi.project.service.ProjectTaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project Task", description = "프로젝트 작업 API")
@RestController
@RequestMapping("/v1/projects/{projectSn}/tasks")
class ProjectTaskController(
    private val projectTaskService: ProjectTaskService
) {

    @Operation(summary = "프로젝트 작업 생성", description = "프로젝트 상세의 작업 목록에 작업을 추가한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 프로젝트가 존재하지 않음")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @RequestBody request: ProjectTaskDto.CreateRequest
    ) = projectTaskService.createTask(principal.userSn, principal.companySn, projectSn, request)

    @Operation(summary = "프로젝트 작업 목록 조회", description = "작업 목록과 상태별 카운트를 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping
    fun getTasks(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) = projectTaskService.getTasks(principal.companySn, projectSn)

    @Operation(summary = "프로젝트 작업 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "작업 또는 프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectTaskSn}")
    fun getTask(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 작업 일련번호", example = "10")
        @PathVariable
        projectTaskSn: Long
    ) = projectTaskService.getTask(principal.companySn, projectSn, projectTaskSn)

    @Operation(summary = "프로젝트 작업 수정", description = "작업 상태가 변경되면 업데이트 피드에 자동 기록한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 작업이 존재하지 않음")
        ]
    )
    @PutMapping("/{projectTaskSn}")
    fun updateTask(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 작업 일련번호", example = "10")
        @PathVariable
        projectTaskSn: Long,
        @RequestBody request: ProjectTaskDto.UpdateRequest
    ) = projectTaskService.updateTask(principal.userSn, principal.companySn, projectSn, projectTaskSn, request)

    @Operation(summary = "프로젝트 작업 상태 변경", description = "상태 변경 내역을 업데이트 피드에 자동 기록한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 상태 또는 작업이 존재하지 않음")
        ]
    )
    @PatchMapping("/{projectTaskSn}/status")
    fun updateTaskStatus(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 작업 일련번호", example = "10")
        @PathVariable
        projectTaskSn: Long,
        @RequestBody request: ProjectTaskDto.StatusRequest
    ) = projectTaskService.updateTaskStatus(principal.userSn, principal.companySn, projectSn, projectTaskSn, request)

    @Operation(summary = "프로젝트 작업 삭제", description = "작업을 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "작업이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectTaskSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 작업 일련번호", example = "10")
        @PathVariable
        projectTaskSn: Long
    ) {
        projectTaskService.deleteTask(principal.companySn, projectSn, projectTaskSn)
    }
}
