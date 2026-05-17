package io.github.timely.timelyapi.board.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class BoardPostDto {
    @Schema(description = "게시글 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 10시에 진행합니다.")
        val content: String
    )

    @Schema(description = "게시글 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "DONE")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내 수정")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 11시에 진행합니다.")
        val content: String
    )

    @Schema(description = "게시글 목록 응답")
    data class SimpleResponse(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorName: String?,

        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "조회 수", example = "12")
        val viewCnt: Long,

        @field:Schema(description = "댓글 수", example = "3")
        val commentCount: Long,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(description = "게시글 상세 응답")
    data class Response(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorName: String?,

        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 10시에 진행합니다.")
        val content: String,

        @field:Schema(description = "조회 수", example = "12")
        val viewCnt: Long,

        @field:Schema(description = "댓글 수", example = "3")
        val commentCount: Long,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )
}
