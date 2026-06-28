package io.github.timely.timelyapi.board.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class BoardPostDto {
    @Schema(name = "BoardPostCreateRequest", description = "게시글 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 10시에 진행합니다.")
        val content: String
    )

    @Schema(name = "BoardPostUpdateRequest", description = "게시글 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "상태 코드", example = "DONE")
        val status: String,

        @field:Schema(description = "제목", example = "주간 회의 안내 수정")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 11시에 진행합니다.")
        val content: String
    )

    @Schema(name = "BoardPostSimpleResponse", description = "게시글 목록 응답")
    data class SimpleResponse(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorName: String?,

        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "카테고리명", example = "공지")
        val categoryName: String?,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "상태명", example = "진행중")
        val statusName: String?,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "본문 미리보기", example = "이번 주 회의는 금요일 오전 10시에 진행합니다.")
        val content: String,

        @field:Schema(description = "조회 수", example = "12")
        val viewCnt: Long,

        @field:Schema(description = "댓글 수", example = "3")
        val commentCount: Long,

        @field:Schema(description = "좋아요 수", example = "5")
        val likeCount: Long,

        @field:Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        val likedByMe: Boolean,

        @field:Schema(description = "현재 사용자의 북마크 여부", example = "true")
        val bookmarkedByMe: Boolean,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "BoardPostResponse", description = "게시글 상세 응답")
    data class Response(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorName: String?,

        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "카테고리명", example = "공지")
        val categoryName: String?,

        @field:Schema(description = "상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "상태명", example = "진행중")
        val statusName: String?,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "본문", example = "이번 주 회의는 금요일 오전 10시에 진행합니다.")
        val content: String,

        @field:Schema(description = "조회 수", example = "12")
        val viewCnt: Long,

        @field:Schema(description = "댓글 수", example = "3")
        val commentCount: Long,

        @field:Schema(description = "좋아요 수", example = "5")
        val likeCount: Long,

        @field:Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        val likedByMe: Boolean,

        @field:Schema(description = "현재 사용자의 북마크 여부", example = "true")
        val bookmarkedByMe: Boolean,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "BoardPostCategoryCountResponse", description = "게시글 카테고리별 건수 응답")
    data class CategoryCountResponse(
        @field:Schema(description = "카테고리 코드. 전체는 ALL", example = "NOTICE")
        val category: String,

        @field:Schema(description = "카테고리명", example = "공지")
        val categoryName: String,

        @field:Schema(description = "게시글 수", example = "2")
        val count: Long
    )

    @Schema(name = "BoardPostRecentNoticeResponse", description = "최근 공지 응답")
    data class RecentNoticeResponse(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "제목", example = "2024년 1분기 프로젝트 계획 공지")
        val title: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?
    )

    @Schema(name = "BoardPostSidebarResponse", description = "게시판 사이드바 집계 응답")
    data class SidebarResponse(
        @field:Schema(description = "게시글 카테고리별 건수. ALL을 포함한다.")
        val categoryCounts: List<CategoryCountResponse>,

        @field:Schema(description = "최근 공지 목록")
        val recentNotices: List<RecentNoticeResponse>,

        @field:Schema(description = "현재 사용자 북마크 요약")
        val bookmarks: BookmarkSummaryResponse,

        @field:Schema(description = "현재 사용자 작성 게시글 요약")
        val authoredPosts: AuthoredPostSummaryResponse
    )

    @Schema(name = "BoardPostBookmarkSummaryResponse", description = "현재 사용자 북마크 요약 응답")
    data class BookmarkSummaryResponse(
        @field:Schema(description = "현재 사용자의 활성 북마크 게시글 수", example = "7")
        val count: Long,

        @field:Schema(description = "최근 북마크한 게시글 목록")
        val recentPosts: List<RecentBookmarkResponse>
    )

    @Schema(name = "BoardPostRecentBookmarkResponse", description = "최근 북마크 게시글 응답")
    data class RecentBookmarkResponse(
        @field:Schema(description = "게시글 일련번호", example = "1")
        val boardPostSn: Long,

        @field:Schema(description = "카테고리 코드", example = "NOTICE")
        val category: String,

        @field:Schema(description = "카테고리명", example = "공지")
        val categoryName: String?,

        @field:Schema(description = "제목", example = "주간 회의 안내")
        val title: String,

        @field:Schema(description = "게시글 생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "북마크 생성일시", example = "2026-05-17T10:00:00")
        val bookmarkedDt: LocalDateTime?
    )

    @Schema(name = "BoardPostAuthoredPostSummaryResponse", description = "현재 사용자 작성 게시글 요약 응답")
    data class AuthoredPostSummaryResponse(
        @field:Schema(description = "현재 사용자가 작성한 활성 게시글 수", example = "5")
        val count: Long
    )
}
