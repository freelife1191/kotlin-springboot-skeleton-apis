package com.skeleton.common.component.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Created by LYT to 2021/05/26
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt")
class SecurityProperties(
    val secretKey: String,
    val expireTime: ExpireTime
) {
    data class ExpireTime(
        val accessToken: Long,
        val refreshToken: Long
    )
}