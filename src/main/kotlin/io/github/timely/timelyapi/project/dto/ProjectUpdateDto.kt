package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class ProjectUpdateDto {
    @Schema(name = "ProjectUpdateCreateRequest", description = "프로젝트 업데이트 작성 요청")
    data class CreateRequest(
        @field:Schema(description = "연결 프로젝트 작업 일련번호", example = "1")
        val projectTaskSn: Long? = null,

        @field:Schema(description = "업데이트 유형 코드", example = "PROGRESS")
        val updateType: String,

        @field:Schema(description = "제목", example = "프론트엔드 개발 60% 완료")
        val title: String,

        @field:Schema(description = "내용", example = "메인 페이지 레이아웃 구성이 완료되었습니다.")
        val content: String? = null
    )

    @Schema(name = "ProjectUpdateUpdateRequest", description = "프로젝트 업데이트 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "연결 프로젝트 작업 일련번호", example = "1")
        val projectTaskSn: Long? = null,

        @field:Schema(description = "업데이트 유형 코드", example = "RISK")
        val updateType: String,

        @field:Schema(description = "제목", example = "백엔드 API 일정 지연 우려")
        val title: String,

        @field:Schema(description = "내용", example = "외부 API 문서 변경으로 일정 지연 가능성이 있습니다.")
        val content: String? = null
    )

    @Schema(name = "ProjectUpdateCommentCreateRequest", description = "프로젝트 업데이트 댓글 생성 요청")
    data class CommentCreateRequest(
        @field:Schema(description = "댓글 내용", example = "확인했습니다.")
        val content: String
    )

    @Schema(name = "ProjectUpdateCommentUpdateRequest", description = "프로젝트 업데이트 댓글 수정 요청")
    data class CommentUpdateRequest(
        @field:Schema(description = "댓글 내용", example = "확인했습니다. 일정 조정하겠습니다.")
        val content: String
    )

    @Schema(name = "ProjectUpdateSummaryResponse", description = "프로젝트 업데이트 목록 응답")
    data class SummaryResponse(
        @field:Schema(description = "프로젝트 업데이트 일련번호", example = "1")
        val projectUpdateSn: Long,

        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorUserNm: String?,

        @field:Schema(description = "연결 프로젝트 작업 일련번호", example = "1")
        val projectTaskSn: Long?,

        @field:Schema(description = "연결 프로젝트 작업명", example = "프론트엔드 개발")
        val projectTaskNm: String?,

        @field:Schema(description = "업데이트 유형 코드", example = "PROGRESS")
        val updateType: String,

        @field:Schema(description = "업데이트 유형명", example = "진행 상황")
        val updateTypeNm: String?,

        @field:Schema(description = "제목", example = "프론트엔드 개발 60% 완료")
        val title: String,

        @field:Schema(description = "내용", example = "메인 페이지 레이아웃 구성이 완료되었습니다.")
        val content: String?,

        @field:Schema(description = "자동 생성 여부", example = "N")
        val autoYn: String,

        @field:Schema(description = "댓글 수", example = "2")
        val commentCount: Long,

        @field:Schema(description = "생성일시", example = "2026-06-27T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-06-27T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "ProjectUpdateResponse", description = "프로젝트 업데이트 상세 응답")
    data class Response(
        @field:Schema(description = "프로젝트 업데이트 일련번호", example = "1")
        val projectUpdateSn: Long,

        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorUserNm: String?,

        @field:Schema(description = "연결 프로젝트 작업 일련번호", example = "1")
        val projectTaskSn: Long?,

        @field:Schema(description = "연결 프로젝트 작업명", example = "프론트엔드 개발")
        val projectTaskNm: String?,

        @field:Schema(description = "업데이트 유형 코드", example = "PROGRESS")
        val updateType: String,

        @field:Schema(description = "업데이트 유형명", example = "진행 상황")
        val updateTypeNm: String?,

        @field:Schema(description = "제목", example = "프론트엔드 개발 60% 완료")
        val title: String,

        @field:Schema(description = "내용", example = "메인 페이지 레이아웃 구성이 완료되었습니다.")
        val content: String?,

        @field:Schema(description = "자동 생성 여부", example = "N")
        val autoYn: String,

        @field:Schema(description = "댓글 목록")
        val comments: List<CommentResponse>,

        @field:Schema(description = "생성일시", example = "2026-06-27T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-06-27T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "ProjectUpdateCommentResponse", description = "프로젝트 업데이트 댓글 응답")
    data class CommentResponse(
        @field:Schema(description = "프로젝트 업데이트 댓글 일련번호", example = "1")
        val projectUpdateCommentSn: Long,

        @field:Schema(description = "프로젝트 업데이트 일련번호", example = "1")
        val projectUpdateSn: Long,

        @field:Schema(description = "작성자 사용자 일련번호", example = "1")
        val authorUserSn: Long,

        @field:Schema(description = "작성자명", example = "김민수")
        val authorUserNm: String?,

        @field:Schema(description = "댓글 내용", example = "확인했습니다.")
        val content: String,

        @field:Schema(description = "생성일시", example = "2026-06-27T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-06-27T10:00:00")
        val updateDt: LocalDateTime?
    )
}
