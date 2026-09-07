package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

class ProjectTaskDto {
    @Schema(name = "ProjectTaskCreateRequest", description = "프로젝트 작업 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "작업명", example = "프론트엔드 개발")
        val taskNm: String,

        @field:Schema(description = "작업 설명", example = "메인 화면과 프로젝트 상세 화면을 구현합니다.")
        val description: String? = null,

        @field:Schema(description = "담당 사용자 일련번호", example = "3")
        val assigneeUserSn: Long? = null,

        @field:Schema(description = "작업 상태 코드", example = "PENDING")
        val status: String = "PENDING",

        @field:Schema(description = "작업 우선순위 코드", example = "HIGH")
        val priority: String = "MEDIUM",

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0,

        @field:Schema(description = "마감일", example = "2026-06-30")
        val dueDt: LocalDate? = null
    )

    @Schema(name = "ProjectTaskUpdateRequest", description = "프로젝트 작업 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "작업명", example = "프론트엔드 개발")
        val taskNm: String,

        @field:Schema(description = "작업 설명", example = "메인 화면과 프로젝트 상세 화면을 구현합니다.")
        val description: String? = null,

        @field:Schema(description = "담당 사용자 일련번호", example = "3")
        val assigneeUserSn: Long? = null,

        @field:Schema(description = "작업 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "작업 우선순위 코드", example = "HIGH")
        val priority: String,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int = 0,

        @field:Schema(description = "마감일", example = "2026-06-30")
        val dueDt: LocalDate? = null
    )

    @Schema(name = "ProjectTaskStatusRequest", description = "프로젝트 작업 상태 변경 요청")
    data class StatusRequest(
        @field:Schema(description = "작업 상태 코드", example = "DONE")
        val status: String
    )

    @Schema(name = "ProjectTaskListResponse", description = "프로젝트 작업 목록 응답")
    data class ListResponse(
        @field:Schema(description = "전체 작업 수", example = "6")
        val totalCount: Long,

        @field:Schema(description = "완료 작업 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "진행중 작업 수", example = "2")
        val inProgressCount: Long,

        @field:Schema(description = "대기 작업 수", example = "2")
        val pendingCount: Long,

        @field:Schema(description = "검토 작업 수", example = "1")
        val reviewCount: Long,

        @field:Schema(description = "작업 목록")
        val tasks: List<Response>
    )

    @Schema(name = "ProjectTaskResponse", description = "프로젝트 작업 응답")
    data class Response(
        @field:Schema(description = "프로젝트 작업 일련번호", example = "1")
        val projectTaskSn: Long,

        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "작업명", example = "프론트엔드 개발")
        val taskNm: String,

        @field:Schema(description = "작업 설명", example = "메인 화면과 프로젝트 상세 화면을 구현합니다.")
        val description: String?,

        @field:Schema(description = "담당 사용자 일련번호", example = "3")
        val assigneeUserSn: Long?,

        @field:Schema(description = "담당 사용자명", example = "박준호")
        val assigneeUserNm: String?,

        @field:Schema(description = "작업 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "작업 상태명", example = "진행중")
        val statusNm: String?,

        @field:Schema(description = "작업 우선순위 코드", example = "HIGH")
        val priority: String,

        @field:Schema(description = "작업 우선순위명", example = "높음")
        val priorityNm: String?,

        @field:Schema(description = "정렬 순서", example = "10")
        val sortSeq: Int,

        @field:Schema(description = "마감일", example = "2026-06-30")
        val dueDt: LocalDate?,

        @field:Schema(description = "완료일시 (Asia/Seoul)", example = "2026-06-27T10:30:00+09:00")
        val completeDt: LocalDateTime?,

        @field:Schema(description = "생성일시 (Asia/Seoul)", example = "2026-06-27T09:00:00+09:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시 (Asia/Seoul)", example = "2026-06-27T10:00:00+09:00")
        val updateDt: LocalDateTime?
    )
}
