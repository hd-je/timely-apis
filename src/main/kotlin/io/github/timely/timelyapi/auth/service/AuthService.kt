package io.github.timely.timelyapi.auth.service

import io.github.timely.timelyapi.auth.dto.AuthDto
import io.github.timely.timelyapi.auth.jwt.JwtProperties
import io.github.timely.timelyapi.auth.jwt.JwtTokenProvider
import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.company.repository.CompanyRepository
import io.github.timely.timelyapi.department.repository.DepartmentRepository
import io.github.timely.timelyapi.position.repository.PositionRepository
import io.github.timely.timelyapi.user.model.TimelyUser
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val departmentRepository: DepartmentRepository,
    private val positionRepository: PositionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) {

    @Transactional
    fun signup(request: AuthDto.SignupRequest): AuthDto.SignupResponse {
        val email = request.email.trim()
        val userName = request.name.trim()
        val phoneNo = request.phoneNo.trim()
        val position = request.position.trim()

        validateSignupRequest(request, email, userName, phoneNo, position)

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

        if (!positionRepository.existsByCompanySnAndPositionCdAndUseYn(request.companySn, position, "Y")) {
            throw IllegalArgumentException("Invalid position")
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = userRepository.save(
            TimelyUser(
                companySn = request.companySn,
                deptSn = request.deptSn,
                email = email,
                loginId = email,
                passwordHash = passwordEncoder.encode(request.password),
                userNm = userName,
                position = position,
                phoneNo = phoneNo,
                avatarUrl = request.avatarUrl?.trim()?.takeIf { it.isNotBlank() },
                userStatus = "ACTIVE",
                useYn = "Y"
            )
        )

        return AuthDto.SignupResponse(
            userSn = user.userSn!!,
            email = user.email,
            userNm = user.userNm,
            userStatus = user.userStatus
        )
    }

    @Transactional(readOnly = true)
    fun login(request: AuthDto.LoginRequest): AuthDto.LoginResponse {
        val email = request.email.trim()
        require(email.matches(EMAIL_PATTERN)) {
            "Invalid email"
        }

        val user = userRepository.findByEmailIgnoreCaseAndUseYn(email, "Y")
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        if (user.userStatus != "ACTIVE") {
            throw IllegalArgumentException("User is not active")
        }

        return AuthDto.LoginResponse(
            accessToken = jwtTokenProvider.createAccessToken(user),
            expiresIn = jwtProperties.accessTokenExpirationMs / 1000,
            userSn = user.userSn!!,
            companySn = user.companySn,
            email = user.email,
            userNm = user.userNm,
            userStatus = user.userStatus
        )
    }

    fun me(principal: TimelyPrincipal): AuthDto.MeResponse {
        return AuthDto.MeResponse(
            userSn = principal.userSn,
            companySn = principal.companySn,
            email = principal.email,
            userNm = principal.userNm,
            userStatus = principal.userStatus
        )
    }

    private fun validateSignupRequest(
        request: AuthDto.SignupRequest,
        email: String,
        userName: String,
        phoneNo: String,
        position: String
    ) {
        require(email.matches(EMAIL_PATTERN)) {
            "Invalid email"
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
        require(position.isNotBlank()) {
            "Invalid position"
        }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val PASSWORD_PATTERN = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
    }
}
