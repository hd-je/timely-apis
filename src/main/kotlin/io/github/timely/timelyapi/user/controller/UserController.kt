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

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "Search users")
    @GetMapping
    fun searchUsers(
        @Parameter(description = "Company serial number", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "Department serial number", example = "2")
        @RequestParam(required = false)
        deptSn: Long?,

        @Parameter(description = "User status", example = "ACTIVE")
        @RequestParam(required = false)
        userStatus: String?,

        @Parameter(description = "User name or email keyword", example = "김민수")
        @RequestParam(required = false)
        keyword: String?
    ) = userService.searchUsers(companySn, deptSn, userStatus, keyword)

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
