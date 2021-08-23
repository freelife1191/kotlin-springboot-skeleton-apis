package com.skeleton.common.config

import com.skeleton.common.component.security.domain.constant.TempToken
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Created by KMS on 2021/03/08.
 */
@Configuration
/*
@OpenAPIDefinition(
    info = io.swagger.v3.oas.annotations.info.Info(
        title = "Skeleton API",
        version = "0.01",
        description = "Documentation Skeleton API v0.01"
    ),
    servers = [Server(
        url = "https://apis-dev.com",
        description = "Development URL"
    )]
)*/
class SwaggerConfig {

    @Value("\${springdoc.app-url}")
    val appUrl: String? =null
    @Value("\${springdoc.app-description}")
    val appDescription: String? =null
    @Value("\${springdoc.app-title}")
    val appTitle: String? =null

    @Bean
    fun customOpenAPI(@Value("\${springdoc.version}") appVersion: String): OpenAPI {
        val basicAuth = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")
            .description("value 란에 토큰을 입력해주세요. \n ${TempToken.JWT}")

        val securityItem = SecurityRequirement().addList("bearer-jwt")

        return OpenAPI()
            .components(Components().addSecuritySchemes("bearer-jwt", basicAuth))
            .addSecurityItem(securityItem)
            .info(
                io.swagger.v3.oas.models.info.Info().title(appTitle).version(appVersion)
                    .license(License().name("Apache 2.0").url("https://springdoc.org"))
            )
            .addServersItem(io.swagger.v3.oas.models.servers.Server()
                .url(appUrl)
                .description(appDescription))
    }
}