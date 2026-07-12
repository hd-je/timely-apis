package io.github.timely.timelyapi.project.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
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

        @field:Schema(description = "프로젝트 우선순위 코드", example = "HIGH")
        val priority: String = "MEDIUM",

        @field:Schema(description = "프로젝트 공개 범위 코드", example = "PRIVATE")
        val visibility: String = "PRIVATE",

        @field:Schema(description = "진행률. 작업 상태 기준으로 서버에서 자동 계산하며 요청값은 사용하지 않음", example = "0")
        val progressRate: Int = 0,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "예산 금액", example = "50000000")
        val budgetAmt: BigDecimal? = null,

        @field:Schema(description = "클라이언트명", example = "(주)테크놀로지")
        val clientNm: String? = null,

        @field:Schema(description = "태그명 목록", example = "[\"웹개발\", \"UI/UX\", \"반응형\"]")
        val tagNames: List<String> = emptyList(),

        @field:Schema(description = "참여 작업자 사용자 일련번호 목록", example = "[2, 3, 4]")
        val memberUserSns: List<Long> = emptyList(),

        @field:Schema(description = "비공개 프로젝트 예외 조회 사용자 일련번호 목록", example = "[5]")
        val accessUserSns: List<Long> = emptyList(),

        @field:Schema(description = "비공개 프로젝트 예외 조회 권한 그룹 코드 목록", example = "[\"ADMIN\", \"LEADER\"]")
        val accessRoleCodes: List<String> = emptyList(),

        @field:Schema(description = "참고 파일 메타데이터 목록")
        val files: List<FileRequest> = emptyList()
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

        @field:Schema(description = "프로젝트 우선순위 코드", example = "HIGH")
        val priority: String = "MEDIUM",

        @field:Schema(description = "프로젝트 공개 범위 코드", example = "PRIVATE")
        val visibility: String = "PRIVATE",

        @field:Schema(description = "진행률. 작업 상태 기준으로 서버에서 자동 계산하며 요청값은 사용하지 않음", example = "68")
        val progressRate: Int = 0,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "예산 금액", example = "50000000")
        val budgetAmt: BigDecimal? = null,

        @field:Schema(description = "클라이언트명", example = "(주)테크놀로지")
        val clientNm: String? = null,

        @field:Schema(description = "태그명 목록", example = "[\"웹개발\", \"UI/UX\", \"반응형\"]")
        val tagNames: List<String> = emptyList(),

        @field:Schema(description = "참여 작업자 사용자 일련번호 목록", example = "[2, 3, 4]")
        val memberUserSns: List<Long> = emptyList(),

        @field:Schema(description = "비공개 프로젝트 예외 조회 사용자 일련번호 목록", example = "[5]")
        val accessUserSns: List<Long> = emptyList(),

        @field:Schema(description = "비공개 프로젝트 예외 조회 권한 그룹 코드 목록", example = "[\"ADMIN\", \"LEADER\"]")
        val accessRoleCodes: List<String> = emptyList(),

        @field:Schema(description = "참고 파일 메타데이터 목록")
        val files: List<FileRequest> = emptyList()
    )

    @Schema(name = "ProjectFileRequest", description = "프로젝트 참고 파일 메타데이터 요청")
    data class FileRequest(
        @field:Schema(description = "원본 파일명", example = "프론트_진행현황.pdf")
        val originalFileNm: String,

        @field:Schema(description = "저장 파일 경로", example = "/files/projects/1/front-status.pdf")
        val storedFilePath: String,

        @field:Schema(description = "파일 크기", example = "2400000")
        val fileSize: Long? = null,

        @field:Schema(description = "콘텐츠 타입", example = "application/pdf")
        val contentType: String? = null
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

        @field:Schema(description = "프로젝트 설명", example = "회사 대표 웹사이트를 새 디자인 시스템 기준으로 개편합니다.")
        val description: String?,

        @field:Schema(description = "프로젝트 상태 코드", example = "IN_PROGRESS")
        val status: String,

        @field:Schema(description = "프로젝트 우선순위 코드", example = "HIGH")
        val priority: String,

        @field:Schema(description = "프로젝트 공개 범위 코드", example = "PRIVATE")
        val visibility: String,

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

        @field:Schema(description = "예산 금액", example = "50000000")
        val budgetAmt: BigDecimal?,

        @field:Schema(description = "클라이언트명", example = "(주)테크놀로지")
        val clientNm: String?,

        @field:Schema(description = "태그 목록")
        val tags: List<TagResponse>,

        @field:Schema(description = "참여자 수", example = "3")
        val memberCount: Int,

        @field:Schema(description = "예외 조회 건수", example = "2")
        val accessExceptionCount: Long,

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

        @field:Schema(description = "프로젝트 우선순위 코드", example = "HIGH")
        val priority: String,

        @field:Schema(description = "프로젝트 공개 범위 코드", example = "PRIVATE")
        val visibility: String,

        @field:Schema(description = "진행률", example = "75")
        val progressRate: Int,

        @field:Schema(description = "시작일", example = "2026-05-01")
        val startDt: LocalDate?,

        @field:Schema(description = "종료일", example = "2026-06-30")
        val endDt: LocalDate?,

        @field:Schema(description = "예산 금액", example = "50000000")
        val budgetAmt: BigDecimal?,

        @field:Schema(description = "클라이언트명", example = "(주)테크놀로지")
        val clientNm: String?,

        @field:Schema(description = "태그 목록")
        val tags: List<TagResponse>,

        @field:Schema(description = "프로젝트 참여자 목록")
        val members: List<MemberResponse>,

        @field:Schema(description = "예외 조회 사용자 목록")
        val accessUsers: List<AccessUserResponse>,

        @field:Schema(description = "예외 조회 권한 그룹 목록")
        val accessRoles: List<AccessRoleResponse>,

        @field:Schema(description = "참고 파일 목록")
        val files: List<FileResponse>,

        @field:Schema(description = "프로젝트 상세 요약")
        val summary: DetailSummaryResponse,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-17T10:00:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "ProjectDetailSummaryResponse", description = "프로젝트 상세 요약 응답")
    data class DetailSummaryResponse(
        @field:Schema(description = "작업 요약")
        val task: TaskSummaryResponse,

        @field:Schema(description = "업데이트 요약")
        val update: UpdateSummaryResponse,

        @field:Schema(description = "마일스톤 요약")
        val milestone: MilestoneSummaryResponse,

        @field:Schema(description = "타임라인 요약")
        val timeline: TimelineSummaryResponse
    )

    @Schema(name = "ProjectTaskSummaryResponse", description = "프로젝트 작업 요약 응답")
    data class TaskSummaryResponse(
        @field:Schema(description = "전체 작업 수", example = "6")
        val totalCount: Long,

        @field:Schema(description = "완료 작업 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "진행중 작업 수", example = "2")
        val inProgressCount: Long,

        @field:Schema(description = "대기 작업 수", example = "2")
        val pendingCount: Long,

        @field:Schema(description = "검토 작업 수", example = "1")
        val reviewCount: Long
    )

    @Schema(name = "ProjectUpdateSummaryCountResponse", description = "프로젝트 업데이트 요약 응답")
    data class UpdateSummaryResponse(
        @field:Schema(description = "전체 업데이트 수", example = "6")
        val totalCount: Long,

        @field:Schema(description = "작업 변경 업데이트 수", example = "2")
        val taskChangeCount: Long,

        @field:Schema(description = "리스크 업데이트 수", example = "1")
        val riskCount: Long
    )

    @Schema(name = "ProjectMilestoneSummaryResponse", description = "프로젝트 마일스톤 요약 응답")
    data class MilestoneSummaryResponse(
        @field:Schema(description = "전체 마일스톤 수", example = "5")
        val totalCount: Long,

        @field:Schema(description = "완료 마일스톤 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "진행중 마일스톤 수", example = "1")
        val inProgressCount: Long,

        @field:Schema(description = "예정 마일스톤 수", example = "2")
        val plannedCount: Long
    )

    @Schema(name = "ProjectTimelineSummaryResponse", description = "프로젝트 타임라인 요약 응답")
    data class TimelineSummaryResponse(
        @field:Schema(description = "전체 단계 수", example = "4")
        val totalCount: Long,

        @field:Schema(description = "완료 단계 수", example = "2")
        val completedCount: Long,

        @field:Schema(description = "진행중 단계 수", example = "1")
        val inProgressCount: Long,

        @field:Schema(description = "예정 단계 수", example = "1")
        val plannedCount: Long
    )

    @Schema(name = "ProjectMemberResponse", description = "프로젝트 참여자 응답")
    data class MemberResponse(
        @field:Schema(description = "프로젝트 참여자 일련번호", example = "1")
        val projectMemberSn: Long,

        @field:Schema(description = "사용자 일련번호", example = "2")
        val userSn: Long,

        @field:Schema(description = "사용자명", example = "이지은")
        val userNm: String?,

        @field:Schema(description = "참여자 역할 코드", example = "WORKER")
        val memberRole: String
    )

    @Schema(name = "ProjectAccessUserResponse", description = "프로젝트 예외 조회 사용자 응답")
    data class AccessUserResponse(
        @field:Schema(description = "프로젝트 예외 조회 사용자 일련번호", example = "1")
        val projectAccessUserSn: Long,

        @field:Schema(description = "사용자 일련번호", example = "5")
        val userSn: Long,

        @field:Schema(description = "사용자명", example = "한소영")
        val userNm: String?
    )

    @Schema(name = "ProjectAccessRoleResponse", description = "프로젝트 예외 조회 권한 그룹 응답")
    data class AccessRoleResponse(
        @field:Schema(description = "프로젝트 예외 조회 권한 그룹 일련번호", example = "1")
        val projectAccessRoleSn: Long,

        @field:Schema(description = "권한 그룹 코드", example = "ADMIN")
        val authorityCd: String
    )

    @Schema(name = "ProjectFileResponse", description = "프로젝트 참고 파일 응답")
    data class FileResponse(
        @field:Schema(description = "프로젝트 파일 일련번호", example = "1")
        val projectFileSn: Long,

        @field:Schema(description = "업로드 사용자 일련번호", example = "1")
        val uploadUserSn: Long,

        @field:Schema(description = "업로드 사용자명", example = "김민수")
        val uploadUserNm: String?,

        @field:Schema(description = "원본 파일명", example = "프론트_진행현황.pdf")
        val originalFileNm: String,

        @field:Schema(description = "저장 파일 경로", example = "/files/projects/1/front-status.pdf")
        val storedFilePath: String,

        @field:Schema(description = "파일 크기", example = "2400000")
        val fileSize: Long?,

        @field:Schema(description = "콘텐츠 타입", example = "application/pdf")
        val contentType: String?,

        @field:Schema(description = "생성일시", example = "2026-05-17T09:00:00")
        val createDt: LocalDateTime?
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
