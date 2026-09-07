package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

class ProjectMilestoneDto {
    @Schema(name = "ProjectMilestoneCreateRequest", description = "프로젝트 마일스톤 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "마일스톤명", example = "MVP 출시")
        val milestoneNm: String,

        @field:Schema(description = "마일스톤 설명", example = "핵심 기능 개발을 완료하고 MVP를 배포합니다.")
        val description: String? = null,

        @field:Schema(description = "마일스톤 상태 코드", example = "PENDING")
        val status: String = "PENDING",

        @field:Schema(description = "마감일", example = "2026-07-31")
        val dueDt: LocalDate? = null,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0
    )

    @Schema(name = "ProjectMilestoneUpdateRequest", description = "프로젝트 마일스톤 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "마일스톤명", example = "MVP 출시")
        val milestoneNm: String,

        @field:Schema(description = "마일스톤 설명", example = "핵심 기능 개발을 완료하고 MVP를 배포합니다.")
        val description: String? = null,

        @field:Schema(description = "마일스톤 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "마감일", example = "2026-07-31")
        val dueDt: LocalDate? = null,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0
    )

    @Schema(name = "ProjectMilestoneListResponse", description = "프로젝트 마일스톤 목록 응답")
    data class ListResponse(
        @field:Schema(description = "전체 마일스톤 수", example = "3")
        val totalCount: Long,

        @field:Schema(description = "마일스톤 목록")
        val milestones: List<Response>
    )

    @Schema(name = "ProjectMilestoneResponse", description = "프로젝트 마일스톤 응답")
    data class Response(
        @field:Schema(description = "프로젝트 마일스톤 일련번호", example = "1")
        val projectMilestoneSn: Long,

        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "마일스톤명", example = "MVP 출시")
        val milestoneNm: String,

        @field:Schema(description = "마일스톤 설명", example = "핵심 기능 개발을 완료하고 MVP를 배포합니다.")
        val description: String?,

        @field:Schema(description = "마일스톤 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "마일스톤 상태명", example = "진행중")
        val statusNm: String?,

        @field:Schema(description = "마감일", example = "2026-07-31")
        val dueDt: LocalDate?,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int,

        @field:Schema(description = "생성일시 (Asia/Seoul)", example = "2026-06-28T09:00:00+09:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시 (Asia/Seoul)", example = "2026-06-28T10:00:00+09:00")
        val updateDt: LocalDateTime?
    )
}
