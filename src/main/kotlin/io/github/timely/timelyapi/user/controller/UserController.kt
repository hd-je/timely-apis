package io.github.timely.timelyapi.user.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    @Operation(
        summary = "회사 직원 목록 검색",
        description = "인증 사용자의 회사에 소속된 활성 직원을 이름 또는 이메일 키워드로 검색한다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 필요")
        ]
    )
    @GetMapping
    fun searchUsers(
        @AuthenticationPrincipal principal: TimelyPrincipal,

        @Parameter(description = "부서 일련번호", example = "2")
        @RequestParam(required = false)
        deptSn: Long?,

        @Parameter(description = "사용자 상태 코드", example = "ACTIVE")
        @RequestParam(required = false)
        userStatus: String?,

        @Parameter(description = "직원명 또는 이메일 검색어", example = "김민수")
        @RequestParam(required = false)
        keyword: String?
    ) = userService.searchUsers(principal.companySn, deptSn, userStatus, keyword)

    @Operation(summary = "Check email duplication")
    @GetMapping("/email/exists")
    fun checkEmail(
        @Parameter(description = "Email to check", example = "kimminsu@example.com")
        @RequestParam
        email: String
    ) =
        userService.checkEmail(email)

    @Operation(summary = "Get user")
    @GetMapping("/{userSn}")
    fun getUser(
        @Parameter(description = "User serial number", example = "1")
        @PathVariable
        userSn: Long
    ) =
        userService.getUser(userSn)
}
