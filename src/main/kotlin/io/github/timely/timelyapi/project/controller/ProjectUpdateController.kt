package io.github.timely.timelyapi.project.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.project.dto.ProjectUpdateDto
import io.github.timely.timelyapi.project.service.ProjectUpdateService
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

@Tag(name = "Project Update", description = "프로젝트 업데이트 피드 API")
@RestController
@RequestMapping("/v1/projects/{projectSn}/updates")
class ProjectUpdateController(
    private val projectUpdateService: ProjectUpdateService
) {

    @Operation(summary = "프로젝트 업데이트 작성", description = "프로젝트 상세의 진행 상황 및 업데이트 피드에 수동 업데이트를 작성한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "작성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 프로젝트가 존재하지 않음")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUpdate(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @RequestBody request: ProjectUpdateDto.CreateRequest
    ) = projectUpdateService.createUpdate(principal.userSn, principal.companySn, projectSn, request)

    @Operation(summary = "프로젝트 업데이트 목록 조회", description = "프로젝트 업데이트 피드를 최신순으로 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping
    fun getUpdates(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long
    ) = projectUpdateService.getUpdates(principal.companySn, projectSn)

    @Operation(summary = "프로젝트 업데이트 상세 조회", description = "업데이트 상세와 댓글 목록을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "업데이트 또는 프로젝트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectUpdateSn}")
    fun getUpdate(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long
    ) = projectUpdateService.getUpdate(principal.companySn, projectSn, projectUpdateSn)

    @Operation(summary = "프로젝트 업데이트 수정", description = "작성자만 수동 업데이트를 수정할 수 있다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 업데이트가 존재하지 않음")
        ]
    )
    @PutMapping("/{projectUpdateSn}")
    fun updateUpdate(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long,
        @RequestBody request: ProjectUpdateDto.UpdateRequest
    ) = projectUpdateService.updateUpdate(principal.userSn, principal.companySn, projectSn, projectUpdateSn, request)

    @Operation(summary = "프로젝트 업데이트 삭제", description = "작성자만 업데이트를 물리 삭제하지 않고 비활성화할 수 있다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "업데이트가 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectUpdateSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUpdate(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long
    ) {
        projectUpdateService.deleteUpdate(principal.userSn, principal.companySn, projectSn, projectUpdateSn)
    }

    @Operation(summary = "프로젝트 업데이트 댓글 작성")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "작성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 업데이트가 존재하지 않음")
        ]
    )
    @PostMapping("/{projectUpdateSn}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long,
        @RequestBody request: ProjectUpdateDto.CommentCreateRequest
    ) = projectUpdateService.createComment(principal.userSn, principal.companySn, projectSn, projectUpdateSn, request)

    @Operation(summary = "프로젝트 업데이트 댓글 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "업데이트가 존재하지 않음")
        ]
    )
    @GetMapping("/{projectUpdateSn}/comments")
    fun getComments(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long
    ) = projectUpdateService.getComments(principal.companySn, projectSn, projectUpdateSn)

    @Operation(summary = "프로젝트 업데이트 댓글 수정", description = "작성자만 댓글을 수정할 수 있다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 댓글이 존재하지 않음")
        ]
    )
    @PutMapping("/{projectUpdateSn}/comments/{projectUpdateCommentSn}")
    fun updateComment(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long,
        @Parameter(description = "프로젝트 업데이트 댓글 일련번호", example = "100")
        @PathVariable
        projectUpdateCommentSn: Long,
        @RequestBody request: ProjectUpdateDto.CommentUpdateRequest
    ) = projectUpdateService.updateComment(
        principal.userSn,
        principal.companySn,
        projectSn,
        projectUpdateSn,
        projectUpdateCommentSn,
        request
    )

    @Operation(summary = "프로젝트 업데이트 댓글 삭제", description = "작성자만 댓글을 물리 삭제하지 않고 비활성화할 수 있다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "댓글이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{projectUpdateSn}/comments/{projectUpdateCommentSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "프로젝트 일련번호", example = "1")
        @PathVariable
        projectSn: Long,
        @Parameter(description = "프로젝트 업데이트 일련번호", example = "10")
        @PathVariable
        projectUpdateSn: Long,
        @Parameter(description = "프로젝트 업데이트 댓글 일련번호", example = "100")
        @PathVariable
        projectUpdateCommentSn: Long
    ) {
        projectUpdateService.deleteComment(
            principal.userSn,
            principal.companySn,
            projectSn,
            projectUpdateSn,
            projectUpdateCommentSn
        )
    }
}
