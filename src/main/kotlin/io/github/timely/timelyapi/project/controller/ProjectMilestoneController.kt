package io.github.timely.timelyapi.project.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.project.dto.ProjectMilestoneDto
import io.github.timely.timelyapi.project.service.ProjectMilestoneService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project Milestone", description = "프로젝트 마일스톤 API")
@RestController
@RequestMapping("/v1/projects/{projectSn}/milestones")
class ProjectMilestoneController(
    private val projectMilestoneService: ProjectMilestoneService
) {

    @Operation(summary = "프로젝트 마일스톤 생성", description = "프로젝트 상세에 마일스톤을 추가한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 프로젝트가 존재하지 않음")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMilestone(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @RequestBody request: ProjectMilestoneDto.CreateRequest
    ) = projectMilestoneService.createMilestone(principal.companySn, projectSn, request)

    @Operation(summary = "프로젝트 마일스톤 목록 조회", description = "프로젝트 상세의 마일스톤 목록을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping
    fun getMilestones(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) = projectMilestoneService.getMilestones(principal.companySn, projectSn)

    @Operation(summary = "프로젝트 마일스톤 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "마일스톤 또는 프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectMilestoneSn}")
    fun getMilestone(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 마일스톤 일련번호", example = "10")
        @PathVariable
        projectMilestoneSn: Long
    ) = projectMilestoneService.getMilestone(principal.companySn, projectSn, projectMilestoneSn)

    @Operation(summary = "프로젝트 마일스톤 수정", description = "프로젝트 상세의 마일스톤 정보를 수정한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 마일스톤이 존재하지 않음")
        ]
    )
    @PutMapping("/{projectMilestoneSn}")
    fun updateMilestone(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 마일스톤 일련번호", example = "10")
        @PathVariable
        projectMilestoneSn: Long,
        @RequestBody request: ProjectMilestoneDto.UpdateRequest
    ) = projectMilestoneService.updateMilestone(principal.companySn, projectSn, projectMilestoneSn, request)

    @Operation(summary = "프로젝트 마일스톤 삭제", description = "마일스톤을 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "마일스톤이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectMilestoneSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMilestone(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 마일스톤 일련번호", example = "10")
        @PathVariable
        projectMilestoneSn: Long
    ) {
        projectMilestoneService.deleteMilestone(principal.companySn, projectSn, projectMilestoneSn)
    }
}
