package io.github.timely.timelyapi.board.controller

import io.github.timely.timelyapi.board.dto.BoardPostDto
import io.github.timely.timelyapi.board.service.BoardPostService
import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.common.PageableFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
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
import org.springframework.security.core.annotation.AuthenticationPrincipal

@Tag(name = "Board Post", description = "게시글 API")
@RestController
@RequestMapping("/v1/board-posts")
class BoardPostController(
    private val boardPostService: BoardPostService
) {

    @Operation(summary = "게시글 생성", description = "게시글을 생성한다. 카테고리와 상태는 활성 공통코드여야 한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestBody request: BoardPostDto.CreateRequest
    ) = boardPostService.createPost(principal.userSn, principal.companySn, request)

    @Operation(summary = "게시글 목록 검색", description = "카테고리, 상태, 키워드 조건으로 게시글을 페이징 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공")
        ]
    )
    @GetMapping
    fun searchPosts(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "카테고리 코드", example = "NOTICE")
        @RequestParam(required = false)
        category: String?,

        @Parameter(description = "상태 코드", example = "IN_PROGRESS")
        @RequestParam(required = false)
        status: String?,

        @Parameter(description = "제목 또는 본문 검색어", example = "회의")
        @RequestParam(required = false)
        keyword: String?,

        @Parameter(description = "페이지 번호. 0부터 시작", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,

        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20")
        size: Int,

        @Parameter(description = "정렬. 허용값: boardPostSn, createDt, viewCnt", example = "createDt,desc")
        @RequestParam(required = false)
        sort: String?
    ) = boardPostService.searchPosts(
        principal.companySn,
        category,
        status,
        keyword,
        PageableFactory.create(
            page = page,
            size = size,
            sort = sort,
            allowedProperties = setOf("boardPostSn", "createDt", "viewCnt"),
            defaultProperty = "boardPostSn"
        )
    )

    @Operation(summary = "게시글 상세 조회", description = "게시글 상세를 조회하고 조회 수를 증가시킨다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "게시글이 존재하지 않음")
        ]
    )
    @GetMapping("/{boardPostSn}")
    fun getPost(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long
    ) = boardPostService.getPost(principal.companySn, boardPostSn)

    @Operation(summary = "게시글 수정")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 게시글이 존재하지 않음")
        ]
    )
    @PutMapping("/{boardPostSn}")
    fun updatePost(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long,
        @RequestBody request: BoardPostDto.UpdateRequest
    ) = boardPostService.updatePost(principal.companySn, boardPostSn, request)

    @Operation(summary = "게시글 삭제", description = "게시글을 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "게시글이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{boardPostSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePost(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "게시글 일련번호", example = "1")
        @PathVariable
        boardPostSn: Long
    ) {
        boardPostService.deletePost(principal.companySn, boardPostSn)
    }
}
