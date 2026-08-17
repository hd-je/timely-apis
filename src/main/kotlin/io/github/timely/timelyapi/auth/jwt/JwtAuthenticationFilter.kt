package io.github.timely.timelyapi.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (HttpMethod.OPTIONS.matches(request.method)) {
            return true
        }

        val path = request.requestURI.removeSuffix("/")
        return request.method.equals(HttpMethod.POST.name(), ignoreCase = true) &&
            path in PUBLIC_AUTH_PATHS
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
            ?.trim()
            ?.takeIf { it.startsWith(BEARER_PREFIX, ignoreCase = true) }
            ?.substring(BEARER_PREFIX.length)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

        if (token != null) {
            runCatching { jwtTokenProvider.parsePrincipal(token) }
                .onSuccess { principal ->
                    val authentication = UsernamePasswordAuthenticationToken(principal, null, emptyList())
                    SecurityContextHolder.getContext().authentication = authentication
                }
                .onFailure { exception ->
                    diagnosticLogger.debug(
                        "Failed to parse JWT for {} {}: {}",
                        request.method,
                        request.requestURI,
                        exception.javaClass.simpleName
                    )
                }
        }

        filterChain.doFilter(request, response)
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        val PUBLIC_AUTH_PATHS = setOf("/v1/auth/signup", "/v1/auth/login")
        val diagnosticLogger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }
}
