package io.github.timely.timelyapi.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

class AuthDto {
    @Schema(description = "회원가입 요청")
    data class SignupRequest(
        @field:Schema(description = "회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "부서 일련번호", example = "2")
        val deptSn: Long,

        @field:Schema(description = "직급 코드", example = "MANAGER")
        val position: String,

        @field:Schema(description = "실명", example = "김민수")
        val name: String,

        @field:Schema(description = "로그인 아이디. 영문/숫자 6자 이상", example = "kimminsu")
        val loginId: String,

        @field:Schema(description = "비밀번호. 영문/숫자/특수문자 포함 8자 이상", example = "Password!123")
        val password: String,

        @field:Schema(description = "비밀번호 확인", example = "Password!123")
        val passwordConfirm: String,

        @field:Schema(description = "전화번호", example = "010-1111-2222")
        val phoneNo: String,

        @field:Schema(description = "이메일", example = "kimminsu@example.com")
        val email: String? = null,

        @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String? = null
    )

    @Schema(description = "회원가입 응답")
    data class SignupResponse(
        @field:Schema(description = "생성된 사용자 일련번호", example = "1")
        val userSn: Long,

        @field:Schema(description = "로그인 아이디", example = "kimminsu")
        val loginId: String,

        @field:Schema(description = "사용자명", example = "김민수")
        val userNm: String,

        @field:Schema(description = "사용자 상태", example = "ACTIVE")
        val userStatus: String
    )
}
