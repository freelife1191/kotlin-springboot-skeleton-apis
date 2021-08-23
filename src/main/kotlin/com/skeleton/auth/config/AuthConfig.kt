package com.skeleton.auth.config

import com.skeleton.auth.client.AuthClient
import com.skeleton.common.component.security.auth.SecurityAuthClient
import com.skeleton.common.component.security.properties.SecurityProperties
import com.skeleton.common.component.security.provider.JwtProvider
import com.skeleton.common.component.security.utils.SystemUtils
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 * Created by LYT to 2021/06/23
 */
@Component
class AuthConfig(
    val securityProperties: SecurityProperties,
    val jwtProvider: JwtProvider,
    val systemUtils: SystemUtils
) {

    @Bean
    fun authClient(): AuthClient =
        SecurityAuthClient(securityProperties, jwtProvider, systemUtils)

}