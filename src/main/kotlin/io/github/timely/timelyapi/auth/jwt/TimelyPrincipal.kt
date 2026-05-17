package io.github.timely.timelyapi.auth.jwt

data class TimelyPrincipal(
    val userSn: Long,
    val companySn: Long,
    val email: String,
    val userNm: String,
    val userStatus: String
)
