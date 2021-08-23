package com.skeleton.common.component.security.auth

import com.skeleton.auth.client.AuthClient
import com.skeleton.common.component.security.domain.constant.TempToken
import com.skeleton.common.component.security.domain.process.ValidTokenInfo
import com.skeleton.common.component.security.properties.SecurityProperties
import com.skeleton.common.component.security.utils.SystemUtils
import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.component.security.provider.JwtProvider
import org.apache.http.auth.AuthenticationException
import org.apache.logging.log4j.LogManager
import javax.servlet.http.HttpServletRequest

/**
 * Created by LYT to 2021/06/23
 */
class SecurityAuthClient(
    val securityProperties: SecurityProperties,
    val jwtProvider: JwtProvider,
    val systemUtils: SystemUtils
) : AuthClient {

    val log = LogManager.getLogger()
    private val TOKEN_PREFIX = "Bearer "

    /**
     * 토큰 검증
     * @param req HttpServletRequest
     * @return CommonResponse<ValidTokenInfo>
     */
    override fun validToken(req: HttpServletRequest): CommonResponse<ValidTokenInfo> =
        validToken(
            getToken(req)
        )

    /**
     * 토큰 검증
     * @param token String
     * @return CommonResponse<ValidTokenInfo>
     */
    override fun validToken(token: String): CommonResponse<ValidTokenInfo> {
        val validToken = jwtProvider.validToken(
            token
        )

        return CommonResponse(
            code = validToken.code,
            message = validToken.message,
            content = ValidTokenInfo(
                tokenInfo = validToken.content,
                token = token
            )
        )
    }

    /**
     * 토큰 조회
     *   - local일 경우 jwt 강제 주입
     *   - local이 아닐 경우 header에서 토큰 조회
     * @param req HttpServletRequest
     * @return String
     */
    private fun getToken(req: HttpServletRequest): String {
        // local 과 my 프로필에 대해서는 임시 JWT 토큰 주입
        if(systemUtils.isLocal() || systemUtils.isMy())
            return TempToken.JWT

        // token 정보 가공
        val token = req.getHeader("Authorization")
            ?: throw AuthenticationException("""header ["Authorization"] must not be null.""")
        return deletePrefix(token)
    }

    private fun deletePrefix(token: String): String = token.replace(TOKEN_PREFIX, "")

}