package io.github.timely.timelyapi.user.service

import io.github.timely.timelyapi.user.dto.UserDto
import io.github.timely.timelyapi.user.model.TimelyUser
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

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
}
