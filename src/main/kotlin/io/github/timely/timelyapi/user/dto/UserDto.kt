package io.github.timely.timelyapi.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class UserDto {
    @Schema(description = "사용자 상세 응답. 비밀번호 해시는 응답하지 않는다.")
    data class Response(
        @field:Schema(description = "사용자 일련번호", example = "1")
        val userSn: Long,

        @field:Schema(description = "소속 회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "소속 부서 일련번호", example = "2")
        val deptSn: Long,

        @field:Schema(description = "로그인 아이디", example = "kimminsu")
        val loginId: String,

        @field:Schema(description = "사용자명", example = "김민수")
        val userNm: String,

        @field:Schema(
            description = "직급 코드. STAFF, SENIOR_STAFF, ASSISTANT_MANAGER, MANAGER, DEPUTY_GENERAL_MANAGER, GENERAL_MANAGER, DIRECTOR, CEO",
            example = "MANAGER"
        )
        val position: String,

        @field:Schema(description = "전화번호", example = "010-1111-2222")
        val phoneNo: String,

        @field:Schema(description = "이메일", example = "kimminsu@example.com")
        val email: String?,

        @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String?,

        @field:Schema(description = "사용자 상태. ACTIVE, PENDING, LOCKED, WITHDRAWN", example = "ACTIVE")
        val userStatus: String,

        @field:Schema(description = "사용 여부. Y: 사용, N: 미사용", example = "Y")
        val useYn: String,

        @field:Schema(description = "생성일시", example = "2026-05-04T16:00:00")
        val createDt: LocalDateTime?,

        @field:Schema(description = "수정일시", example = "2026-05-04T16:30:00")
        val updateDt: LocalDateTime?
    )

    @Schema(description = "사용자 목록 응답")
    data class SimpleResponse(
        @field:Schema(description = "사용자 일련번호", example = "1")
        val userSn: Long,

        @field:Schema(description = "소속 회사 일련번호", example = "1")
        val companySn: Long,

        @field:Schema(description = "소속 부서 일련번호", example = "2")
        val deptSn: Long,

        @field:Schema(description = "로그인 아이디", example = "kimminsu")
        val loginId: String,

        @field:Schema(description = "사용자명", example = "김민수")
        val userNm: String,

        @field:Schema(description = "직급 코드", example = "MANAGER")
        val position: String,

        @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar/1.png")
        val avatarUrl: String?,

        @field:Schema(description = "사용자 상태", example = "ACTIVE")
        val userStatus: String
    )

    @Schema(description = "로그인 아이디 중복 확인 응답")
    data class LoginIdExistsResponse(
        @field:Schema(description = "확인한 로그인 아이디", example = "kimminsu")
        val loginId: String,

        @field:Schema(description = "중복 여부. true면 이미 사용 중", example = "true")
        val exists: Boolean
    )
}
