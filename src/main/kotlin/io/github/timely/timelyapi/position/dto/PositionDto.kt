package io.github.timely.timelyapi.position.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class PositionDto {
    @Schema(name = "PositionCreateRequest", description = "Position create request")
    data class CreateRequest(
        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Position code", example = "LEAD")
        val positionCd: String,

        @field:Schema(description = "Position name", example = "팀장")
        val positionNm: String,

        @field:Schema(description = "Sort order", example = "1")
        val sortOrd: Int = 0,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String = "Y"
    )

    @Schema(name = "PositionUpdateRequest", description = "Position update request")
    data class UpdateRequest(
        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Position code", example = "LEAD")
        val positionCd: String,

        @field:Schema(description = "Position name", example = "팀장")
        val positionNm: String,

        @field:Schema(description = "Sort order", example = "1")
        val sortOrd: Int = 0,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String = "Y"
    )

    @Schema(name = "PositionUseYnRequest", description = "Use flag update request")
    data class UseYnRequest(
        @field:Schema(description = "Use flag", example = "N")
        val useYn: String
    )

    @Schema(name = "PositionResponse", description = "Position response")
    data class Response(
        @field:Schema(description = "Position serial number", example = "1")
        val positionSn: Long,

        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Position code", example = "LEAD")
        val positionCd: String,

        @field:Schema(description = "Position name", example = "팀장")
        val positionNm: String,

        @field:Schema(description = "Sort order", example = "1")
        val sortOrd: Int,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String,

        @field:Schema(description = "Created date time (Asia/Seoul)", example = "2026-05-04T16:00:00+09:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "Updated date time (Asia/Seoul)", example = "2026-05-04T16:30:00+09:00")
        val updateDt: LocalDateTime?
    )
}
