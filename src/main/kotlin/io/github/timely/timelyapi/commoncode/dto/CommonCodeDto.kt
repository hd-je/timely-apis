package io.github.timely.timelyapi.commoncode.dto

import io.swagger.v3.oas.annotations.media.Schema

class CommonCodeDto {
    @Schema(description = "공통코드 목록 응답")
    data class Response(
        @field:Schema(description = "코드 그룹", example = "BOARD_CATEGORY")
        val codeGroup: String,

        @field:Schema(description = "코드", example = "NOTICE")
        val code: String,

        @field:Schema(description = "코드명", example = "공지")
        val codeNm: String,

        @field:Schema(description = "정렬 순서", example = "1")
        val sortSeq: Int
    )
}
