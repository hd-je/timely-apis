package io.github.timely.timelyapi.common

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "API 에러 응답")
data class ApiErrorResponse(
    @field:Schema(description = "에러 발생 일시", example = "2026-05-04T18:00:00")
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @field:Schema(description = "HTTP 상태 코드", example = "400")
    val status: Int,

    @field:Schema(description = "에러 메시지", example = "Login ID already exists")
    val message: String
)
