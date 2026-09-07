package io.github.timely.timelyapi.home.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.home.service.HomeDashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Home Dashboard", description = "홈 대시보드 API")
@RestController
@RequestMapping("/v1/home")
class HomeDashboardController(
    private val homeDashboardService: HomeDashboardService
) {

    @Operation(summary = "홈 대시보드 조회", description = "첫 화면에 표시할 프로젝트와 게시판 요약 정보를 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요")
        ]
    )
    @GetMapping("/dashboard")
    fun getDashboard(
        @Parameter(hidden = true)
        @AuthenticationPrincipal
        principal: TimelyPrincipal,
        @Parameter(description = "나에게 배정된 프로젝트 목록 건수. 최대 10건", example = "5")
        @RequestParam(defaultValue = "5")
        projectSize: Int,
        @Parameter(description = "최근 활동 건수. 최대 10건", example = "10")
        @RequestParam(defaultValue = "10")
        activitySize: Int
    ) = homeDashboardService.getDashboard(
        userSn = principal.userSn,
        companySn = principal.companySn,
        projectSize = projectSize,
        activitySize = activitySize
    )
}
