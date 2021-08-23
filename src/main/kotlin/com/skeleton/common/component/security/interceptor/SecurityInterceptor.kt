package com.skeleton.common.component.security.interceptor

import com.skeleton.auth.client.AuthClient
import io.jsonwebtoken.JwtException
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by LYT to 2021/06/23
 */
@Component
class SecurityInterceptor(
    val authClient: AuthClient
): HandlerInterceptor {

    val log = LogManager.getLogger()

    /**
     * 토큰 검증
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Any
     * @return Boolean
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = authClient.validToken(request)
        if (token.code != HttpStatus.OK.value()) {
            log.warn(token.message)
            throw JwtException(token.message)
        }
        return true
    }
}