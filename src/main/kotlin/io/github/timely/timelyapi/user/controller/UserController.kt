package io.github.timely.timelyapi.user.controller

import io.github.timely.timelyapi.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "사용자 목록 검색")
    @GetMapping
    fun searchUsers(
        @Parameter(description = "회사 일련번호. 입력하면 해당 회사 사용자만 조회", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "부서 일련번호. 입력하면 해당 부서 사용자만 조회", example = "2")
        @RequestParam(required = false)
        deptSn: Long?,

        @Parameter(description = "사용자 상태. ACTIVE, PENDING, LOCKED, WITHDRAWN", example = "ACTIVE")
        @RequestParam(required = false)
        userStatus: String?,

        @Parameter(description = "로그인 아이디, 사용자명, 이메일 검색어", example = "김민수")
        @RequestParam(required = false)
        keyword: String?
    ) = userService.searchUsers(companySn, deptSn, userStatus, keyword)

    @Operation(summary = "로그인 아이디 중복 확인")
    @GetMapping("/login-id/exists")
    fun checkLoginId(
        @Parameter(description = "중복 확인할 로그인 아이디", example = "kimminsu")
        @RequestParam
        loginId: String
    ) =
        userService.checkLoginId(loginId)

    @Operation(summary = "사용자 상세 조회")
    @GetMapping("/{userSn}")
    fun getUser(
        @Parameter(description = "사용자 일련번호", example = "1")
        @PathVariable
        userSn: Long
    ) =
        userService.getUser(userSn)
}
