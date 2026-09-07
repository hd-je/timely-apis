package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

class ProjectTimelineDto {
    @Schema(name = "ProjectTimelineCreateRequest", description = "프로젝트 타임라인 단계 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "단계명", example = "기획")
        val phaseNm: String,

        @field:Schema(description = "단계 설명", example = "요구사항을 정리하고 화면 흐름을 확정합니다.")
        val description: String? = null,

        @field:Schema(description = "단계 상태 코드", example = "PLANNED")
        val status: String = "PLANNED",

        @field:Schema(description = "시작일", example = "2026-07-01")
        val startDt: LocalDate? = null,

        @field:Schema(description = "종료일", example = "2026-07-10")
        val endDt: LocalDate? = null,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0
    )

    @Schema(name = "ProjectTimelineUpdateRequest", description = "프로젝트 타임라인 단계 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "단계명", example = "기획")
        val phaseNm: String,

        @field:Schema(description = "단계 설명", example = "요구사항을 정리하고 화면 흐름을 확정합니다.")
        val description: String? = null,

        @field:Schema(description = "단계 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "시작일", example = "2026-07-01")
        val startDt: LocalDate? = null,

        @field:Schema(description = "종료일", example = "2026-07-10")
        val endDt: LocalDate? = null,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0
    )

    @Schema(name = "ProjectTimelineResponse", description = "프로젝트 타임라인 단계 응답")
    data class Response(
        @field:Schema(description = "프로젝트 타임라인 일련번호", example = "1")
        val projectTimelineSn: Long,

        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "단계명", example = "기획")
        val phaseNm: String,

        @field:Schema(description = "단계 설명", example = "요구사항을 정리하고 화면 흐름을 확정합니다.")
        val description: String?,

        @field:Schema(description = "단계 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "단계 상태명", example = "진행중")
        val statusNm: String?,

        @field:Schema(description = "시작일", example = "2026-07-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-07-10")
        val endDt: LocalDate?,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int,

        @field:Schema(description = "생성일시 (Asia/Seoul)", example = "2026-06-28T09:00:00+09:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시 (Asia/Seoul)", example = "2026-06-28T10:00:00+09:00")
        val updateDt: LocalDateTime?
    )
}
