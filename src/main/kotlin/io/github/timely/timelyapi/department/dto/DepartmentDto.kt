package io.github.timely.timelyapi.department.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class DepartmentDto {
    @Schema(name = "DepartmentCreateRequest", description = "Department create request")
    data class CreateRequest(
        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Department name", example = "개발팀")
        val deptNm: String,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String = "Y"
    )

    @Schema(name = "DepartmentUpdateRequest", description = "Department update request")
    data class UpdateRequest(
        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Department name", example = "개발팀")
        val deptNm: String,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String = "Y"
    )

    @Schema(name = "DepartmentUseYnRequest", description = "Use flag update request")
    data class UseYnRequest(
        @field:Schema(description = "Use flag", example = "N")
        val useYn: String
    )

    @Schema(name = "DepartmentResponse", description = "Department detail response")
    data class Response(
        @field:Schema(description = "Department serial number", example = "1")
        val deptSn: Long,

        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long?,

        @field:Schema(description = "Department name", example = "개발팀")
        val deptNm: String,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String,

        @field:Schema(description = "Created date time", example = "2026-05-04T16:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "Updated date time", example = "2026-05-04T16:30:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "DepartmentSimpleResponse", description = "Department list response")
    data class SimpleResponse(
        @field:Schema(description = "Department serial number", example = "1")
        val deptSn: Long,

        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long?,

        @field:Schema(description = "Department name", example = "개발팀")
        val deptNm: String
    )
}
