package io.github.timely.timelyapi.home.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class HomeDashboardDto {
    @Schema(name = "HomeDashboardResponse", description = "홈 대시보드 응답")
    data class Response(
        @field:Schema(description = "프로젝트 요약")
        val projects: ProjectSummaryResponse,

        @field:Schema(description = "게시판 요약")
        val boards: BoardSummaryResponse
    )

    @Schema(name = "HomeProjectSummaryResponse", description = "홈 프로젝트 요약 응답")
    data class ProjectSummaryResponse(
        @field:Schema(description = "프로젝트 상태별 건수")
        val statusCounts: ProjectStatusCountsResponse
    )

    @Schema(name = "HomeProjectStatusCountsResponse", description = "홈 프로젝트 상태별 건수 응답")
    data class ProjectStatusCountsResponse(
        @field:Schema(description = "전체 프로젝트 수", example = "8")
        val totalCount: Long,

        @field:Schema(description = "진행중 프로젝트 수", example = "4")
        val inProgressCount: Long,

        @field:Schema(description = "완료 프로젝트 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "보류 프로젝트 수", example = "2")
        val onHoldCount: Long
    )

    @Schema(name = "HomeBoardSummaryResponse", description = "홈 게시판 요약 응답")
    data class BoardSummaryResponse(
        @field:Schema(description = "게시글 카테고리별 건수")
        val categoryCounts: List<BoardCategoryCountResponse>,

        @field:Schema(description = "최근 공지 목록")
        val recentNotices: List<RecentNoticeResponse>
    )

    @Schema(name = "HomeBoardCategoryCountResponse", description = "홈 게시글 카테고리별 건수 응답")
    data class BoardCategoryCountResponse(
        @field:Schema(description = "카테고리 코드. 전체는 ALL", example = "NOTICE")
        val category: String,

        @field:Schema(description = "카테고리명", example = "공지")
        val categoryName: String,

        @field:Schema(description = "게시글 수", example = "2")
        val count: Long
    )

    @Schema(name = "HomeRecentNoticeResponse", description = "홈 최근 공지 응답")
    data class RecentNoticeResponse(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "제목", example = "2026년 1분기 프로젝트 계획 공지")
        val title: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?
    )
}
