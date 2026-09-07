package io.github.timely.timelyapi.user.service

import io.github.timely.timelyapi.department.repository.DepartmentRepository
import io.github.timely.timelyapi.position.repository.PositionRepository
import io.github.timely.timelyapi.user.dto.UserDto
import io.github.timely.timelyapi.user.model.TimelyUser
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
    private val positionRepository: PositionRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun updateMyProfile(
        userSn: Long,
        companySn: Long,
        request: UserDto.ProfileUpdateRequest
    ): UserDto.Response {
        val user = getActiveCompanyUser(userSn, companySn)

        request.userNm?.let {
            val userName = it.trim()
            require(userName.isNotBlank()) { "Name is required" }
            user.userNm = userName
        }

        request.deptSn?.let { deptSn ->
            departmentRepository.findByDeptSnAndCompanySnAndUseYn(deptSn, companySn, "Y")
                ?: throw IllegalArgumentException("Department is not active or does not belong to the company")
            user.deptSn = deptSn
        }

        request.position?.let {
            val position = it.trim()
            require(position.isNotBlank()) { "Invalid position" }
            require(positionRepository.existsByCompanySnAndPositionCdAndUseYn(companySn, position, "Y")) {
                "Invalid position"
            }
            user.position = position
        }

        request.phoneNo?.let {
            val phoneNo = it.trim()
            require(phoneNo.isNotBlank()) { "Phone number is required" }
            user.phoneNo = phoneNo
        }

        return user.toResponse()
    }

    @Transactional
    fun updateMyPassword(
        userSn: Long,
        companySn: Long,
        request: UserDto.PasswordUpdateRequest
    ): UserDto.PasswordUpdateResponse {
        val user = getActiveCompanyUser(userSn, companySn)

        require(passwordEncoder.matches(request.currentPassword, user.passwordHash)) {
            "Current password does not match"
        }
        require(request.newPassword.matches(PASSWORD_PATTERN)) {
            "Password must be at least 8 characters and include letters, numbers, and special characters"
        }
        require(request.newPassword == request.newPasswordConfirm) {
            "Password confirmation does not match"
        }

        user.passwordHash = passwordEncoder.encode(request.newPassword)
        return UserDto.PasswordUpdateResponse(message = "Password updated")
    }

    @Transactional(readOnly = true)
    fun getUser(userSn: Long): UserDto.Response {
        val user = userRepository.findById(userSn)
            .orElseThrow { IllegalArgumentException("User not found") }

        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchUsers(
        companySn: Long?,
        deptSn: Long?,
        userStatus: String?,
        keyword: String?
    ): List<UserDto.SimpleResponse> {
        return userRepository.searchActiveUsers(
            companySn = companySn,
            deptSn = deptSn,
            userStatus = userStatus?.takeIf { it.isNotBlank() },
            keyword = keyword?.takeIf { it.isNotBlank() }
        ).map { it.toSimpleResponse() }
    }

    @Transactional(readOnly = true)
    fun checkEmail(email: String): UserDto.EmailExistsResponse {
        val normalizedEmail = email.trim()
        return UserDto.EmailExistsResponse(
            email = normalizedEmail,
            exists = userRepository.existsByEmailIgnoreCase(normalizedEmail)
        )
    }

    private fun getActiveCompanyUser(userSn: Long, companySn: Long): TimelyUser =
        userRepository.findByUserSnAndCompanySnAndUseYn(userSn, companySn, "Y")
            ?: throw IllegalArgumentException("User not found")

    private fun TimelyUser.toSimpleResponse() =
        UserDto.SimpleResponse(
            userSn = userSn!!,
            companySn = companySn,
            deptSn = deptSn,
            userNm = userNm,
            position = position,
            email = email,
            avatarUrl = avatarUrl,
            userStatus = userStatus
        )

    private fun TimelyUser.toResponse() =
        UserDto.Response(
            userSn = userSn!!,
            companySn = companySn,
            deptSn = deptSn,
            userNm = userNm,
            position = position,
            phoneNo = phoneNo,
            email = email,
            avatarUrl = avatarUrl,
            userStatus = userStatus,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )

    companion object {
        private val PASSWORD_PATTERN = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
    }
}
