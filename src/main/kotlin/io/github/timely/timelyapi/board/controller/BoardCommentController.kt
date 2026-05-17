package io.github.timely.timelyapi.board.controller

import io.github.timely.timelyapi.board.dto.BoardCommentDto
import io.github.timely.timelyapi.board.service.BoardCommentService
import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal

@Tag(name = "Board Comment", description = "댓글 API")
@RestController
@RequestMapping("/v1/board-posts/{boardPostSn}/comments")
class BoardCommentController(
    private val boardCommentService: BoardCommentService
) {

    @Operation(summary = "댓글 생성")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 게시글이 존재하지 않음")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @RequestBody request: BoardCommentDto.CreateRequest
    ) = boardCommentService.createComment(principal.userSn, boardPostSn, request)

    @Operation(summary = "댓글 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "게시글이 존재하지 않음")
        ]
    )
    @GetMapping
    fun searchComments(
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @PageableDefault(size = 20, sort = ["boardCommentSn"])
        pageable: Pageable
    ) = boardCommentService.searchComments(boardPostSn, pageable)

    @Operation(summary = "댓글 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "댓글 또는 게시글이 존재하지 않음")
        ]
    )
    @GetMapping("/{boardCommentSn}")
    fun getComment(
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @Parameter(description = "댓글 일련번호", example = "10")
        @PathVariable
        boardCommentSn: Long
    ) = boardCommentService.getComment(boardPostSn, boardCommentSn)

    @Operation(summary = "댓글 수정")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 댓글이 존재하지 않음")
        ]
    )
    @PutMapping("/{boardCommentSn}")
    fun updateComment(
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @Parameter(description = "댓글 일련번호", example = "10")
        @PathVariable
        boardCommentSn: Long,
        @RequestBody request: BoardCommentDto.UpdateRequest
    ) = boardCommentService.updateComment(boardPostSn, boardCommentSn, request)

    @Operation(summary = "댓글 삭제", description = "댓글을 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "댓글이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{boardCommentSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @Parameter(description = "댓글 일련번호", example = "10")
        @PathVariable
        boardCommentSn: Long
    ) {
        boardCommentService.deleteComment(boardPostSn, boardCommentSn)
    }
}
