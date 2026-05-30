package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

class ProjectDto {
    @Schema(name = "ProjectCreateRequest", description = "프로젝트 생성 요청")
    data class CreateRequest(
        @field:Schema(description = "프로젝트명", example = "웹사이트 리뉴얼 프로젝트")
        val projectNm: String,

        @field:Schema(description = "프로젝트 설명", example = "회사 대표 웹사이트를 새 디자인 시스템 기준으로 개편합니다.")
        val description: String?,

        @field:Schema(description = "책임자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "프로젝트 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "진행률. 0부터 100까지 입력", example = "75")
        val progressRate: Int,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "태그명 목록", example = "[\"웹개발\", \"UI/UX\", \"반응형\"]")
        val tagNames: List<String> = emptyList()
    )

    @Schema(name = "ProjectUpdateRequest", description = "프로젝트 수정 요청")
    data class UpdateRequest(
        @field:Schema(description = "프로젝트명", example = "웹사이트 리뉴얼 프로젝트")
        val projectNm: String,

        @field:Schema(description = "프로젝트 설명", example = "회사 대표 웹사이트를 새 디자인 시스템 기준으로 개편합니다.")
        val description: String?,

        @field:Schema(description = "책임자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "프로젝트 상태 코드", example = "COMPLETED")
        val status: String,

        @field:Schema(description = "진행률. 0부터 100까지 입력", example = "100")
        val progressRate: Int,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "태그명 목록", example = "[\"웹개발\", \"UI/UX\", \"반응형\"]")
        val tagNames: List<String> = emptyList()
    )

    @Schema(name = "ProjectTagResponse", description = "프로젝트 태그 응답")
    data class TagResponse(
        @field:Schema(description = "프로젝트 태그 일련번호", example = "1")
        val projectTagSn: Long,

        @field:Schema(description = "태그명", example = "웹개발")
        val tagNm: String
    )

    @Schema(name = "ProjectSimpleResponse", description = "프로젝트 목록 응답")
    data class SimpleResponse(
        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "프로젝트명", example = "웹사이트 리뉴얼 프로젝트")
        val projectNm: String,

        @field:Schema(description = "프로젝트 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "진행률", example = "75")
        val progressRate: Int,

        @field:Schema(description = "책임자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "책임자명", example = "김민수")
        val ownerUserNm: String?,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "태그 목록")
        val tags: List<TagResponse>,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "ProjectResponse", description = "프로젝트 상세 응답")
    data class Response(
        @field:Schema(description = "프로젝트 일련번호", example = "1")
        val projectSn: Long,

        @field:Schema(description = "프로젝트명", example = "웹사이트 리뉴얼 프로젝트")
        val projectNm: String,

        @field:Schema(description = "프로젝트 설명", example = "회사 대표 웹사이트를 새 디자인 시스템 기준으로 개편합니다.")
        val description: String?,

        @field:Schema(description = "책임자 사용자 일련번호", example = "1")
        val ownerUserSn: Long,

        @field:Schema(description = "책임자명", example = "김민수")
        val ownerUserNm: String?,

        @field:Schema(description = "프로젝트 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "진행률", example = "75")
        val progressRate: Int,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "태그 목록")
        val tags: List<TagResponse>,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "ProjectStatusCountsResponse", description = "프로젝트 상태별 카운트 응답")
    data class StatusCountsResponse(
        @field:Schema(description = "전체 프로젝트 수", example = "8")
        val totalCount: Long,

        @field:Schema(description = "진행중 프로젝트 수", example = "4")
        val inProgressCount: Long,

        @field:Schema(description = "완료 프로젝트 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "보류 프로젝트 수", example = "2")
        val onHoldCount: Long
    )
}
