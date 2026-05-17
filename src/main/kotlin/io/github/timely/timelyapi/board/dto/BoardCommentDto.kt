package io.github.timely.timelyapi.board.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class BoardCommentDto {
    @Schema(name = "BoardCommentCreateRequest", description = "댓글 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "댓글 내용", example = "확인했습니다.")
        val content: String
    )

    @Schema(name = "BoardCommentUpdateRequest", description = "댓글 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "댓글 내용", example = "확인했습니다. 참석하겠습니다.")
        val content: String
    )

    @Schema(name = "BoardCommentResponse", description = "댓글 응답")
    data class Response(
        @field:Schema(description = "댓글 일련번호", example = "1")
        val boardCommentSn: Long,

        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "댓글 내용", example = "확인했습니다.")
        val content: String,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:30:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:30:00")
        val updateDt: LocalDateTime?
    )
}
