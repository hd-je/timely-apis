package io.github.timely.timelyapi.home.dto

import io.github.timely.timelyapi.project.dto.ProjectDto
import io.github.timely.timelyapi.schedule.dto.ScheduleDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class HomeDashboardDto {
    @Schema(name = "HomeDashboardResponse", description = "홈 대시보드 응답")
    data class Response(
        @field:Schema(description = "프로젝트 요약")
        val projects: ProjectSummaryResponse,

        @field:Schema(description = "게시판 요약")
        val boards: BoardSummaryResponse,

        @field:Schema(description = "현재 사용자에게 배정된 프로젝트 요약")
        val assignedProjects: AssignedProjectSummaryResponse,

        @field:Schema(description = "오늘부터 7일 이내의 내 일정")
        val upcomingSchedules: List<ScheduleDto.Response>,

        @field:Schema(description = "게시글과 배정 프로젝트 업데이트를 합친 최근 활동")
        val recentActivities: List<RecentActivityResponse>
    )

    @Schema(name = "HomeAssignedProjectSummaryResponse", description = "나에게 배정된 프로젝트 요약")
    data class AssignedProjectSummaryResponse(
        @field:Schema(description = "나에게 배정된 활성 프로젝트 전체 건수", example = "4")
        val totalCount: Long,

        @field:Schema(description = "나에게 배정된 프로젝트 목록")
        val projects: List<ProjectDto.SimpleResponse>
    )

    @Schema(name = "HomeRecentActivityResponse", description = "홈 최근 활동")
    data class RecentActivityResponse(
        @field:Schema(description = "활동 유형", example = "BOARD_POST", allowableValues = ["BOARD_POST", "PROJECT_UPDATE"])
        val activityType: String,

        @field:Schema(description = "활동 대상 일련번호", example = "10")
        val targetSn: Long,

        @field:Schema(description = "연결 프로젝트 일련번호. 게시글은 null", example = "3")
        val projectSn: Long?,

        @field:Schema(description = "연결 프로젝트명. 게시글은 null", example = "웹사이트 리뉴얼")
        val projectNm: String?,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorName: String?,

        @field:Schema(description = "활동 제목", example = "주간 진행 상황")
        val title: String,

        @field:Schema(description = "활동 내용")
        val content: String?,

        @field:Schema(description = "생성일시 (Asia/Seoul)", example = "2026-08-28T18:07:00+09:00")
        val createDt: LocalDateTime?
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

        @field:Schema(description = "생성일시 (Asia/Seoul)", example = "2026-05-17T09:00:00+09:00")
        val createDt: LocalDateTime?
    )
}
