package io.github.timely.timelyapi.project.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.common.PageableFactory
import io.github.timely.timelyapi.project.dto.ProjectDto
import io.github.timely.timelyapi.project.service.ProjectService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project", description = "프로젝트 API")
@RestController
@RequestMapping("/v1/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @Operation(summary = "프로젝트 생성", description = "프로젝트를 생성하고 태그를 저장한다. 상태는 활성 공통코드여야 한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProject(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestBody request: ProjectDto.CreateRequest
    ) = projectService.createProject(principal.companySn, request)

    @Operation(summary = "프로젝트 목록 검색", description = "상태와 키워드 조건으로 프로젝트를 페이징 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 상태 코드")
        ]
    )
    @GetMapping
    fun searchProjects(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 상태 코드", example = "IN_PROGRESS")
        @RequestParam(required = false)
        status: String?,

        @Parameter(description = "프로젝트명 또는 설명 검색어", example = "리뉴얼")
        @RequestParam(required = false)
        keyword: String?,

        @Parameter(description = "페이지 번호. 0부터 시작", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,

        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20")
        size: Int,

        @Parameter(description = "정렬. 허용값: projectSn, projectNm, status, progressRate, startDt, endDt, createDt", example = "createDt,desc")
        @RequestParam(required = false)
        sort: String?
    ) = projectService.searchProjects(
        companySn = principal.companySn,
        status = status,
        keyword = keyword,
        pageable = PageableFactory.create(
            page = page,
            size = size,
            sort = sort,
            allowedProperties = setOf("projectSn", "projectNm", "status", "progressRate", "startDt", "endDt", "createDt"),
            defaultProperty = "projectSn"
        )
    )

    @Operation(summary = "프로젝트 상태별 카운트 조회", description = "전체, 진행중, 완료, 보류 프로젝트 수를 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공")
        ]
    )
    @GetMapping("/status-counts")
    fun getStatusCounts(
        @AuthenticationPrincipal principal: TimelyPrincipal
    ) = projectService.getStatusCounts(principal.companySn)

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 상세와 태그 목록을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectSn}")
    fun getProject(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) = projectService.getProject(principal.companySn, projectSn)

    @Operation(summary = "프로젝트 수정", description = "프로젝트 기본 정보와 태그를 수정한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 프로젝트가 존재하지 않음")
        ]
    )
    @PutMapping("/{projectSn}")
    fun updateProject(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @RequestBody request: ProjectDto.UpdateRequest
    ) = projectService.updateProject(principal.companySn, projectSn, request)

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProject(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) {
        projectService.deleteProject(principal.companySn, projectSn)
    }
}
