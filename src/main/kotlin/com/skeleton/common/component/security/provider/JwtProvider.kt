package com.skeleton.common.component.security.provider


import com.skeleton.common.domain.response.CommonResponse
import com.skeleton.common.component.security.properties.SecurityProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Created by LYT to 2021/05/26
 */
@Component
class JwtProvider(
    val securityProperties: SecurityProperties
) {
    val log = LogManager.getLogger()

    /**
     * 토큰 생성
     */
    fun createToken(subject: String, data: Map<String, Any?>, expireTime: Long): String {
        val now = LocalDateTime.now()
        val expire = now.plusSeconds(expireTime)
        var jwt = Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .setExpiration(Date.from(expire.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(Keys.hmacShaKeyFor(securityProperties.secretKey.toByteArray()))
        return setJwtClaimAndVO(jwt, data)
    }

    /**
     * Jwt 데이터 세팅
     * @param jwt JwtBuilder
     * @param data HashMap<String, Any>
     * @return String
     */
    private fun setJwtClaimAndVO(jwt: JwtBuilder, data: Map<String, Any?>): String {
        data.map {
            jwt.claim(it.key, it.value)
        }
        return jwt.compact()

    }

    /**
     * 토큰 검사 및 복호화
     * @param token String
     * @return CommonResponse<HashMap<(kotlin.String..kotlin.String?), (kotlin.Any..kotlin.Any?)>>
     */
    fun validToken(token: String): CommonResponse<HashMap<String, Any>> =
        try {
            // validation && get token info
            val tokenData = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(securityProperties.secretKey.toByteArray()))
                .build()
                .parseClaimsJws(token)
                .body.toMap() as HashMap

            log.debug("token: $tokenData")

            CommonResponse(
                tokenData
            )
        } catch (e: SecurityException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "Invalid JWT Signature"
            )
        } catch (e: MalformedJwtException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "Invalid JWT Token"
            )
        } catch (e: ExpiredJwtException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "Expired JWT Token"
            )
        } catch (e: UnsupportedJwtException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "Unsupported JWT Token"
            )
        } catch (e: IllegalArgumentException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "JWT Token compact of handler are invalid."
            )
        } catch (e: SecurityException) {
            CommonResponse(
                httpStatus = HttpStatus.UNAUTHORIZED,
                message = "JWT signature does not match locally computed signature."
            )
        }
}