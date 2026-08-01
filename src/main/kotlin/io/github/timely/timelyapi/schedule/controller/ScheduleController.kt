package io.github.timely.timelyapi.schedule.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.common.PageableFactory
import io.github.timely.timelyapi.schedule.dto.ScheduleDto
import io.github.timely.timelyapi.schedule.service.ScheduleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@Tag(name = "Schedule", description = "일정 API")
@RestController
@RequestMapping("/v1/schedules")
class ScheduleController(
    private val scheduleService: ScheduleService
) {

    private val scheduleSortProperties = setOf("scheduleSn", "startDt", "endDt", "title", "scheduleType", "status", "createDt")

    @Operation(summary = "일정 생성", description = "인증 사용자의 회사 범위에 일정을 생성한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSchedule(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestBody request: ScheduleDto.CreateRequest
    ) = scheduleService.createSchedule(principal.userSn, principal.companySn, request)

    @Operation(summary = "일정 목록 검색", description = "startDt/endDt는 페이징 조회하고, from/to 날짜 범위는 해당 기간의 전체 일정을 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 검색 조건")
        ]
    )
    @GetMapping
    fun searchSchedules(
        @AuthenticationPrincipal principal: TimelyPrincipal,

        @Parameter(description = "검색 시작일시. from/to를 사용하지 않을 때 필수", example = "2026-07-01T00:00:00")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        startDt: LocalDateTime?,

        @Parameter(description = "검색 종료일시. from/to를 사용하지 않을 때 필수", example = "2026-07-31T23:59:59")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        endDt: LocalDateTime?,

        @Parameter(description = "전체 조회 시작일. to와 함께 사용", example = "2026-06-29")
        @RequestParam(name = "from", required = false)
        from: LocalDate?,

        @Parameter(description = "전체 조회 종료일. from과 함께 사용", example = "2026-08-02")
        @RequestParam(name = "to", required = false)
        to: LocalDate?,

        @Parameter(description = "프로젝트 일련번호", example = "10")
        @RequestParam(required = false)
        projectSn: Long?,

        @Parameter(description = "소유자 또는 참석자 사용자 일련번호", example = "3")
        @RequestParam(required = false)
        userSn: Long?,

        @Parameter(description = "일정 유형 코드", example = "MEETING")
        @RequestParam(required = false)
        scheduleType: String?,

        @Parameter(description = "일정 상태 코드", example = "PLANNED")
        @RequestParam(required = false)
        status: String?,

        @Parameter(description = "페이지 번호. 0부터 시작", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,

        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20")
        size: Int,

        @Parameter(description = "정렬. 허용값: scheduleSn, startDt, endDt, title, scheduleType, status, createDt", example = "startDt,asc")
        @RequestParam(required = false)
        sort: String?
    ): Any {
        val range = resolveRange(startDt, endDt, from, to)
        val response = scheduleService.searchSchedules(
            companySn = principal.companySn,
            startDt = range.first,
            endDt = range.second,
            projectSn = projectSn,
            userSn = userSn,
            scheduleType = scheduleType,
            status = status,
            pageable = pageable(page, size, sort, from, to)
        )
        return if (from != null) response.content else response
    }

    @Operation(summary = "내 일정 목록 검색", description = "인증 사용자가 소유자이거나 참석자인 일정을 조회한다. from/to 사용 시 페이지네이션 없이 전체 반환한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 검색 조건")
        ]
    )
    @GetMapping("/my")
    fun searchMySchedules(
        @AuthenticationPrincipal principal: TimelyPrincipal,

        @Parameter(description = "검색 시작일시", example = "2026-07-01T00:00:00")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        startDt: LocalDateTime?,

        @Parameter(description = "검색 종료일시", example = "2026-07-31T23:59:59")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        endDt: LocalDateTime?,

        @Parameter(description = "검색 시작일", example = "2026-06-29")
        @RequestParam(name = "from", required = false)
        from: LocalDate?,

        @Parameter(description = "검색 종료일", example = "2026-08-02")
        @RequestParam(name = "to", required = false)
        to: LocalDate?,

        @Parameter(description = "프로젝트 일련번호", example = "10")
        @RequestParam(required = false)
        projectSn: Long?,

        @Parameter(description = "일정 유형 코드", example = "MEETING")
        @RequestParam(required = false)
        scheduleType: String?,

        @Parameter(description = "일정 상태 코드", example = "PLANNED")
        @RequestParam(required = false)
        status: String?,

        @Parameter(description = "페이지 번호. 0부터 시작", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,

        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20")
        size: Int,

        @Parameter(description = "정렬. 허용값: scheduleSn, startDt, endDt, title, scheduleType, status, createDt", example = "startDt,asc")
        @RequestParam(required = false)
        sort: String?
    ): Any {
        val range = resolveRange(startDt, endDt, from, to)
        val response = scheduleService.searchMySchedules(
            companySn = principal.companySn,
            userSn = principal.userSn,
            startDt = range.first,
            endDt = range.second,
            projectSn = projectSn,
            scheduleType = scheduleType,
            status = status,
            pageable = pageable(page, size, sort, from, to)
        )
        return if (from != null) response.content else response
    }

    @Operation(summary = "내 예정 일정 조회", description = "오늘 00:00부터 7일 후 23:59:59까지 겹치는 내 일정을 전체 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요")
        ]
    )
    @GetMapping("/my/upcoming")
    fun searchUpcomingSchedules(
        @AuthenticationPrincipal principal: TimelyPrincipal
    ) = scheduleService.searchUpcomingSchedules(principal.companySn, principal.userSn)

    @Operation(summary = "팀 일정 목록 검색", description = "대상 부서 활성 사용자의 일정을 조회한다. 연차와 출장도 일정 유형으로 포함하며 from/to 사용 시 전체 반환한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 검색 조건 또는 부서가 존재하지 않음")
        ]
    )
    @GetMapping("/team")
    fun searchTeamSchedules(
        @AuthenticationPrincipal principal: TimelyPrincipal,

        @Parameter(description = "검색 시작일시", example = "2026-07-01T00:00:00")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        startDt: LocalDateTime?,

        @Parameter(description = "검색 종료일시", example = "2026-07-31T23:59:59")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        endDt: LocalDateTime?,

        @Parameter(description = "검색 시작일", example = "2026-06-29")
        @RequestParam(name = "from", required = false)
        from: LocalDate?,

        @Parameter(description = "검색 종료일", example = "2026-08-02")
        @RequestParam(name = "to", required = false)
        to: LocalDate?,

        @Parameter(description = "부서 일련번호. 생략하면 인증 사용자의 부서를 사용", example = "2")
        @RequestParam(required = false)
        deptSn: Long?,

        @Parameter(description = "프로젝트 일련번호", example = "10")
        @RequestParam(required = false)
        projectSn: Long?,

        @Parameter(description = "일정 유형 코드", example = "MEETING")
        @RequestParam(required = false)
        scheduleType: String?,

        @Parameter(description = "일정 상태 코드", example = "PLANNED")
        @RequestParam(required = false)
        status: String?,

        @Parameter(description = "페이지 번호. 0부터 시작", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,

        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20")
        size: Int,

        @Parameter(description = "정렬. 허용값: scheduleSn, startDt, endDt, title, scheduleType, status, createDt", example = "startDt,asc")
        @RequestParam(required = false)
        sort: String?
    ): Any {
        val range = resolveRange(startDt, endDt, from, to)
        val response = scheduleService.searchTeamSchedules(
            companySn = principal.companySn,
            userSn = principal.userSn,
            deptSn = deptSn,
            startDt = range.first,
            endDt = range.second,
            projectSn = projectSn,
            scheduleType = scheduleType,
            status = status,
            pageable = pageable(page, size, sort, from, to)
        )
        return if (from != null) response.content else response
    }

    @Operation(summary = "일정 상세 조회", description = "회사 범위 안의 활성 일정 상세를 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "일정이 존재하지 않음")
        ]
    )
    @GetMapping("/{scheduleSn}")
    fun getSchedule(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "일정 일련번호", example = "1")
        @PathVariable
        scheduleSn: Long
    ) = scheduleService.getSchedule(principal.companySn, scheduleSn)

    @Operation(summary = "일정 수정", description = "회사 범위 안의 활성 일정을 수정한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 일정이 존재하지 않음")
        ]
    )
    @PutMapping("/{scheduleSn}")
    fun updateSchedule(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "일정 일련번호", example = "1")
        @PathVariable
        scheduleSn: Long,
        @RequestBody request: ScheduleDto.UpdateRequest
    ) = scheduleService.updateSchedule(principal.companySn, scheduleSn, request)

    @Operation(summary = "일정 상태 변경", description = "회사 범위 안의 활성 일정 상태만 변경한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 상태 또는 일정이 존재하지 않음")
        ]
    )
    @PatchMapping("/{scheduleSn}/status")
    fun updateScheduleStatus(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "일정 일련번호", example = "1")
        @PathVariable
        scheduleSn: Long,
        @RequestBody request: ScheduleDto.StatusRequest
    ) = scheduleService.updateScheduleStatus(principal.companySn, scheduleSn, request)

    @Operation(summary = "일정 삭제", description = "일정을 물리 삭제하지 않고 비활성화한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "400", description = "일정이 존재하지 않음")
        ]
    )
    @DeleteMapping("/{scheduleSn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSchedule(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @Parameter(description = "일정 일련번호", example = "1")
        @PathVariable
        scheduleSn: Long
    ) {
        scheduleService.deleteSchedule(principal.companySn, scheduleSn)
    }

    private fun resolveRange(
        startDt: LocalDateTime?,
        endDt: LocalDateTime?,
        from: LocalDate?,
        to: LocalDate?
    ): Pair<LocalDateTime, LocalDateTime> {
        require((from == null) == (to == null)) { "from and to must be provided together" }
        return if (from != null && to != null) {
            from.atStartOfDay() to to.plusDays(1).atStartOfDay().minusNanos(1)
        } else {
            require(startDt != null && endDt != null) { "startDt and endDt are required" }
            startDt to endDt
        }
    }

    private fun pageable(page: Int, size: Int, sort: String?, from: LocalDate?, to: LocalDate?): Pageable {
        val pageable = PageableFactory.create(
            page = page,
            size = size,
            sort = sort,
            allowedProperties = scheduleSortProperties,
            defaultProperty = "startDt",
            defaultDirection = org.springframework.data.domain.Sort.Direction.ASC
        )
        return if (from != null || to != null) Pageable.unpaged(pageable.sort) else pageable
    }
}
