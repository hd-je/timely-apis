package io.github.timely.timelyapi.schedule.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class ScheduleDto {
    @Schema(name = "ScheduleCreateRequest", description = "일정 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "일정 제목", example = "프로젝트 킥오프 미팅")
        val title: String,

        @field:Schema(description = "일정 내용", example = "프로젝트 범위와 역할을 확정합니다.")
        val content: String? = null,

        @field:Schema(description = "일정 유형 코드", example = "MEETING")
        val scheduleType: String,

        @field:Schema(description = "일정 상태 코드", example = "PLANNED")
        val status: String = "PLANNED",

        @field:Schema(description = "시작일시", example = "2026-07-01T09:00:00")
        val startDt: LocalDateTime,

        @field:Schema(description = "종료일시", example = "2026-07-01T10:00:00")
        val endDt: LocalDateTime,

        @field:Schema(description = "종일 여부", example = "N")
        val allDayYn: String = "N",

        @field:Schema(description = "소유자 사용자 일련번호. 미입력 시 인증 사용자로 저장", example = "1")
        val ownerUserSn: Long? = null,

        @field:Schema(description = "연결 프로젝트 일련번호", example = "10")
        val projectSn: Long? = null,

        @field:Schema(description = "장소", example = "서울 본사 3층 회의실")
        val place: String? = null,

        @field:Schema(description = "참석자 사용자 일련번호 목록", example = "[2, 3, 4]")
        val participantUserSns: List<Long> = emptyList()
    )

    @Schema(name = "ScheduleUpdateRequest", description = "일정 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "일정 제목", example = "프로젝트 킥오프 미팅")
        val title: String,

        @field:Schema(description = "일정 내용", example = "프로젝트 범위와 역할을 확정합니다.")
        val content: String? = null,

        @field:Schema(description = "일정 유형 코드", example = "MEETING")
        val scheduleType: String,

        @field:Schema(description = "일정 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "시작일시", example = "2026-07-01T09:00:00")
        val startDt: LocalDateTime,

        @field:Schema(description = "종료일시", example = "2026-07-01T10:00:00")
        val endDt: LocalDateTime,

        @field:Schema(description = "종일 여부", example = "N")
        val allDayYn: String = "N",

        @field:Schema(description = "소유자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "연결 프로젝트 일련번호", example = "10")
        val projectSn: Long? = null,

        @field:Schema(description = "장소", example = "서울 본사 3층 회의실")
        val place: String? = null,

        @field:Schema(description = "참석자 사용자 일련번호 목록", example = "[2, 3, 4]")
        val participantUserSns: List<Long> = emptyList()
    )

    @Schema(name = "ScheduleStatusRequest", description = "일정 상태 변경 요청")
    data class StatusRequest(
        @field:Schema(description = "일정 상태 코드", example = "COMPLETED")
        val status: String
    )

    @Schema(name = "ScheduleParticipantResponse", description = "일정 참석자 응답")
    data class ParticipantResponse(
        @field:Schema(description = "사용자 일련번호", example = "2")
        val userSn: Long,

        @field:Schema(description = "사용자명", example = "박준호")
        val userNm: String?
    )

    @Schema(name = "ScheduleResponse", description = "일정 응답")
    data class Response(
        @field:Schema(description = "일정 일련번호", example = "1")
        val scheduleSn: Long,

        @field:Schema(description = "회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "연결 프로젝트 일련번호", example = "10")
        val projectSn: Long?,

        @field:Schema(description = "연결 프로젝트명", example = "웹사이트 리뉴얼 프로젝트")
        val projectNm: String?,

        @field:Schema(description = "소유자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "소유자명", example = "김민수")
        val ownerUserNm: String?,

        @field:Schema(description = "일정 제목", example = "프로젝트 킥오프 미팅")
        val title: String,

        @field:Schema(description = "일정 내용", example = "프로젝트 범위와 역할을 확정합니다.")
        val content: String?,

        @field:Schema(description = "일정 유형 코드", example = "MEETING")
        val scheduleType: String,

        @field:Schema(description = "일정 유형명", example = "회의")
        val scheduleTypeNm: String?,

        @field:Schema(description = "일정 상태 코드", example = "PLANNED")
        val status: String,

        @field:Schema(description = "일정 상태명", example = "예정")
        val statusNm: String?,

        @field:Schema(description = "시작일시", example = "2026-07-01T09:00:00")
        val startDt: LocalDateTime,

        @field:Schema(description = "종료일시", example = "2026-07-01T10:00:00")
        val endDt: LocalDateTime,

        @field:Schema(description = "종일 여부", example = "N")
        val allDayYn: String,

        @field:Schema(description = "장소", example = "서울 본사 3층 회의실")
        val place: String?,

        @field:Schema(description = "참석자 목록")
        val participants: List<ParticipantResponse>,

        @field:Schema(description = "생성일시", example = "2026-06-28T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-06-28T10:00:00")
        val updateDt: LocalDateTime?
    )
}
