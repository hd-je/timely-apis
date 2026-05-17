package io.github.timely.timelyapi.common

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "공통 페이징 응답")
data class PageResponse<T>(
    @field:Schema(description = "현재 페이지 데이터")
    val content: List<T>,

    @field:Schema(description = "현재 페이지 번호. 0부터 시작", example = "0")
    val page: Int,

    @field:Schema(description = "페이지 크기", example = "20")
    val size: Int,

    @field:Schema(description = "전체 건수", example = "100")
    val totalElements: Long,

    @field:Schema(description = "전체 페이지 수", example = "5")
    val totalPages: Int,

    @field:Schema(description = "첫 페이지 여부", example = "true")
    val first: Boolean,

    @field:Schema(description = "마지막 페이지 여부", example = "false")
    val last: Boolean
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> =
            PageResponse(
                content = page.content,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                first = page.isFirst,
                last = page.isLast
            )
    }
}
