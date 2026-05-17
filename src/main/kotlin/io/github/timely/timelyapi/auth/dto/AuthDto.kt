package io.github.timely.timelyapi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

class AuthDto {
    @Schema(description = "Signup request")
    data class SignupRequest(
        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Department serial number", example = "2")
        val deptSn: Long,

        @field:Schema(description = "Position code", example = "LEAD")
        val position: String,

        @field:Schema(description = "Name", example = "김민수")
        val name: String,

        @field:Schema(description = "Password. At least 8 characters including letters, numbers, and special characters", example = "Password!123")
        val password: String,

        @field:Schema(description = "Password confirmation", example = "Password!123")
        val passwordConfirm: String,

        @field:Schema(description = "Phone number", example = "010-1111-2222")
        val phoneNo: String,

        @field:Schema(description = "Email used as login ID", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "Profile image URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String? = null
    )

    @Schema(description = "Signup response")
    data class SignupResponse(
        @field:Schema(description = "Created user serial number", example = "1")
        val userSn: Long,

        @field:Schema(description = "Email used as login ID", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "User name", example = "김민수")
        val userNm: String,

        @field:Schema(description = "User status", example = "ACTIVE")
        val userStatus: String
    )

    @Schema(description = "Login request")
    data class LoginRequest(
        @field:Schema(description = "Email", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "Password", example = "Password!123")
        val password: String
    )

    @Schema(description = "Login response")
    data class LoginResponse(
        @field:Schema(description = "JWT access token")
        val accessToken: String,

        @field:Schema(description = "토큰 타입", example = "Bearer")
        val tokenType: String = "Bearer",

        @field:Schema(description = "만료 시간(초)", example = "3600")
        val expiresIn: Long,

        @field:Schema(description = "User serial number", example = "1")
        val userSn: Long,

        @field:Schema(description = "Email", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "User name", example = "김민수")
        val userNm: String,

        @field:Schema(description = "User status", example = "ACTIVE")
        val userStatus: String
    )

    @Schema(description = "Current authenticated user response")
    data class MeResponse(
        @field:Schema(description = "User serial number", example = "1")
        val userSn: Long,

        @field:Schema(description = "Email", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "User name", example = "김민수")
        val userNm: String,

        @field:Schema(description = "User status", example = "ACTIVE")
        val userStatus: String
    )
}
