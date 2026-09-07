package io.github.timely.timelyapi.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class UserDto {
    @Schema(name = "UserProfileUpdateRequest", description = "Authenticated user profile update request")
    data class ProfileUpdateRequest(
        @field:Schema(description = "User name", example = "김민수")
        val userNm: String? = null,

        @field:Schema(description = "Department serial number", example = "2")
        val deptSn: Long? = null,

        @field:Schema(description = "Position code", example = "MANAGER")
        val position: String? = null,

        @field:Schema(description = "Phone number", example = "010-1111-2222")
        val phoneNo: String? = null
    )

    @Schema(name = "UserPasswordUpdateRequest", description = "Authenticated user password update request")
    data class PasswordUpdateRequest(
        @field:Schema(description = "Current password", example = "Current1!")
        val currentPassword: String,

        @field:Schema(
            description = "New password. At least 8 characters including letters, numbers, and special characters",
            example = "NewPassword1!"
        )
        val newPassword: String,

        @field:Schema(description = "New password confirmation", example = "NewPassword1!")
        val newPasswordConfirm: String
    )

    @Schema(name = "UserPasswordUpdateResponse", description = "Password update response")
    data class PasswordUpdateResponse(
        @field:Schema(description = "Password update result message", example = "Password updated")
        val message: String
    )

    @Schema(name = "UserResponse", description = "User detail response")
    data class Response(
        @field:Schema(description = "User serial number", example = "1")
        val userSn: Long,

        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Department serial number", example = "2")
        val deptSn: Long,

        @field:Schema(description = "User name", example = "김민수")
        val userNm: String,

        @field:Schema(description = "Position code", example = "LEAD")
        val position: String,

        @field:Schema(description = "Phone number", example = "010-1111-2222")
        val phoneNo: String,

        @field:Schema(description = "Email used as login ID", example = "kimminsu@example.com")
        val email: String?,

        @field:Schema(description = "Profile image URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String?,

        @field:Schema(description = "User status", example = "ACTIVE")
        val userStatus: String,

        @field:Schema(description = "Use flag", example = "Y")
        val useYn: String,

        @field:Schema(description = "Created date time (Asia/Seoul)", example = "2026-05-04T16:00:00+09:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "Updated date time (Asia/Seoul)", example = "2026-05-04T16:30:00+09:00")
        val updateDt: LocalDateTime?
    )

    @Schema(name = "UserSimpleResponse", description = "User list response")
    data class SimpleResponse(
        @field:Schema(description = "User serial number", example = "1")
        val userSn: Long,

        @field:Schema(description = "Company serial number", example = "1")
        val companySn: Long,

        @field:Schema(description = "Department serial number", example = "2")
        val deptSn: Long,

        @field:Schema(description = "User name", example = "김민수")
        val userNm: String,

        @field:Schema(description = "Position code", example = "LEAD")
        val position: String,

        @field:Schema(description = "Email used as login ID", example = "kimminsu@example.com")
        val email: String?,

        @field:Schema(description = "Profile image URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String?,

        @field:Schema(description = "User status", example = "ACTIVE")
        val userStatus: String
    )

    @Schema(name = "UserEmailExistsResponse", description = "Email exists response")
    data class EmailExistsResponse(
        @field:Schema(description = "Email", example = "kimminsu@example.com")
        val email: String,

        @field:Schema(description = "Whether the email already exists", example = "true")
        val exists: Boolean
    )
}
