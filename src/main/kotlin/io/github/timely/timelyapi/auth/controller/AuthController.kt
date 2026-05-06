package io.github.timely.timelyapi.auth.controller

import io.github.timely.timelyapi.auth.dto.AuthDto
import io.github.timely.timelyapi.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "Auth API")
@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "Signup")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    fun signup(@RequestBody request: AuthDto.SignupRequest) =
        authService.signup(request)

    @Operation(summary = "Login")
    @PostMapping("/login")
    fun login(@RequestBody request: AuthDto.LoginRequest) =
        authService.login(request)
}
