package io.github.timely.timelyapi.auth.service

import io.github.timely.timelyapi.auth.dto.AuthDto
import io.github.timely.timelyapi.company.repository.CompanyRepository
import io.github.timely.timelyapi.department.repository.DepartmentRepository
import io.github.timely.timelyapi.user.model.TimelyUser
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val departmentRepository: DepartmentRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    fun signup(request: AuthDto.SignupRequest): AuthDto.SignupResponse {
        val loginId = request.loginId.trim()
        val userName = request.name.trim()
        val phoneNo = request.phoneNo.trim()
        val position = request.position.trim()

        validateSignupRequest(request, loginId, userName, phoneNo, position)

        val company = companyRepository.findById(request.companySn)
            .orElseThrow { IllegalArgumentException("Company not found") }

        if (company.useYn != "Y") {
            throw IllegalArgumentException("Company is not active")
        }

        val department = departmentRepository.findById(request.deptSn)
            .orElseThrow { IllegalArgumentException("Department not found") }

        if (department.useYn != "Y") {
            throw IllegalArgumentException("Department is not active")
        }

        if (department.companySn != null && department.companySn != company.companySn) {
            throw IllegalArgumentException("Department does not belong to the company")
        }

        if (userRepository.existsByLoginId(loginId)) {
            throw IllegalArgumentException("Login ID already exists")
        }

        val user = userRepository.save(
            TimelyUser(
                companySn = request.companySn,
                deptSn = request.deptSn,
                loginId = loginId,
                passwordHash = passwordEncoder.encode(request.password),
                userNm = userName,
                position = position,
                phoneNo = phoneNo,
                email = request.email?.trim()?.takeIf { it.isNotBlank() },
                avatarUrl = request.avatarUrl?.trim()?.takeIf { it.isNotBlank() },
                userStatus = "ACTIVE",
                useYn = "Y"
            )
        )

        return AuthDto.SignupResponse(
            userSn = user.userSn!!,
            loginId = user.loginId,
            userNm = user.userNm,
            userStatus = user.userStatus
        )
    }

    private fun validateSignupRequest(
        request: AuthDto.SignupRequest,
        loginId: String,
        userName: String,
        phoneNo: String,
        position: String
    ) {
        require(loginId.matches(LOGIN_ID_PATTERN)) {
            "Login ID must be at least 6 characters and contain only letters and numbers"
        }
        require(request.password.matches(PASSWORD_PATTERN)) {
            "Password must be at least 8 characters and include letters, numbers, and special characters"
        }
        require(request.password == request.passwordConfirm) {
            "Password confirmation does not match"
        }
        require(userName.isNotBlank()) {
            "Name is required"
        }
        require(phoneNo.isNotBlank()) {
            "Phone number is required"
        }
        require(position in POSITIONS) {
            "Invalid position"
        }
    }

    companion object {
        private val LOGIN_ID_PATTERN = Regex("^[A-Za-z0-9]{6,50}$")
        private val PASSWORD_PATTERN = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
        private val POSITIONS = setOf(
            "STAFF",
            "SENIOR_STAFF",
            "ASSISTANT_MANAGER",
            "MANAGER",
            "DEPUTY_GENERAL_MANAGER",
            "GENERAL_MANAGER",
            "DIRECTOR",
            "CEO"
        )
    }
}
