package io.github.timely.timelyapi.project.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.project.dto.ProjectTimelineDto
import io.github.timely.timelyapi.project.service.ProjectTimelineService
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

@Tag(name = "Project Timeline", description = "프로젝트 타임라인 API")
@RestController
@RequestMapping("/v1/projects/{projectSn}/timelines")
class ProjectTimelineController(
    private val projectTimelineService: ProjectTimelineService
) {

    @Operation(summary = "프로젝트 타임라인 단계 생성", description = "프로젝트 상세의 타임라인에 단계를 추가한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 프로젝트가 존재하지 않음")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTimeline(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @RequestBody request: ProjectTimelineDto.CreateRequest
    ) = projectTimelineService.createTimeline(principal.companySn, projectSn, request)

    @Operation(summary = "프로젝트 타임라인 목록 조회", description = "프로젝트 상세의 타임라인 단계 목록을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping
    fun getTimelines(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) = projectTimelineService.getTimelines(principal.companySn, projectSn)

    @Operation(summary = "프로젝트 타임라인 단계 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "타임라인 단계 또는 프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectTimelineSn}")
    fun getTimeline(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 타임라인 일련번호", example = "10")
        @PathVariable
        projectTimelineSn: Long
    ) = projectTimelineService.getTimeline(principal.companySn, projectSn, projectTimelineSn)

    @Operation(summary = "프로젝트 타임라인 단계 수정")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 타임라인 단계가 존재하지 않음")
        ]
    )
    @PutMapping("/{projectTimelineSn}")
    fun updateTimeline(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 타임라인 일련번호", example = "10")
        @PathVariable
        projectTimelineSn: Long,
        @RequestBody request: ProjectTimelineDto.UpdateRequest
    ) = projectTimelineService.updateTimeline(principal.companySn, projectSn, projectTimelineSn, request)

    @Operation(summary = "프로젝트 타임라인 단계 삭제", description = "타임라인 단계를 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "타임라인 단계가 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectTimelineSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTimeline(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 타임라인 일련번호", example = "10")
        @PathVariable
        projectTimelineSn: Long
    ) {
        projectTimelineService.deleteTimeline(principal.companySn, projectSn, projectTimelineSn)
    }
}
