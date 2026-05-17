package io.github.timely.timelyapi.auth.jwt

import io.github.timely.timelyapi.user.model.TimelyUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val signingKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(user: TimelyUser): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpirationMs)

        return Jwts.builder()
            .subject(user.userSn!!.toString())
            .claim("email", user.email)
            .claim("userNm", user.userNm)
            .claim("userStatus", user.userStatus)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact()
    }

    fun parsePrincipal(token: String): TimelyPrincipal {
        val claims = parseClaims(token)
        return TimelyPrincipal(
            userSn = claims.subject.toLong(),
            email = claims["email", String::class.java],
            userNm = claims["userNm", String::class.java],
            userStatus = claims["userStatus", String::class.java]
        )
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
