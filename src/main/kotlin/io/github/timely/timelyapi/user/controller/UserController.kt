package io.github.timely.timelyapi.user.controller

import io.github.timely.timelyapi.auth.jwt.TimelyPrincipal
import io.github.timely.timelyapi.user.dto.UserDto
import io.github.timely.timelyapi.user.service.UserService
import io.github.timely.timelyapi.user.service.UserAvatarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService,
    private val userAvatarService: UserAvatarService
) {

    @Operation(summary = "내 프로필 이미지 업로드", description = "최대 3MB 이미지를 업로드하고 현재 사용자의 avatarUrl을 변경한다.")
    @PostMapping("/me/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadMyAvatar(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestPart("file") file: MultipartFile
    ) = userAvatarService.upload(principal.userSn, principal.companySn, file)

    @Operation(
        summary = "내 개인정보 변경",
        description = "인증 사용자의 이름, 부서, 직급, 전화번호를 변경한다. 전달하지 않은 항목은 유지한다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "변경 성공"),
            ApiResponse(responseCode = "400", description = "유효하지 않은 부서, 직급 또는 입력값"),
            ApiResponse(responseCode = "401", description = "인증 필요")
        ]
    )
    @PatchMapping("/me")
    fun updateMyProfile(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestBody request: UserDto.ProfileUpdateRequest
    ) = userService.updateMyProfile(principal.userSn, principal.companySn, request)

    @Operation(
        summary = "내 비밀번호 변경",
        description = "현재 비밀번호를 확인한 후 새 비밀번호로 변경한다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "변경 성공"),
            ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치 또는 새 비밀번호 정책 위반"),
            ApiResponse(responseCode = "401", description = "인증 필요")
        ]
    )
    @PutMapping("/me/password")
    fun updateMyPassword(
        @AuthenticationPrincipal principal: TimelyPrincipal,
        @RequestBody request: UserDto.PasswordUpdateRequest
    ) = userService.updateMyPassword(principal.userSn, principal.companySn, request)

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
