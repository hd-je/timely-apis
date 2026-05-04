package io.github.timely.timelyapi.company.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class CompanyDto {
    @Schema(description = "회사 상세 응답")
    data class Response(
        @field:Schema(description = "회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "회사명", example = "삼성전자 주식회사")
        val companyNm: String,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-04T16:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-04T16:30:00")
        val updateDt: LocalDateTime?
    )

    @Schema(description = "회사 목록 응답")
    data class SimpleResponse(
        @field:Schema(description = "회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "회사명", example = "삼성전자 주식회사")
        val companyNm: String
    )
}
