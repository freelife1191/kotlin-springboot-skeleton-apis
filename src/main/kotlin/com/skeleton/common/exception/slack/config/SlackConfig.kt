package com.skeleton.common.exception.slack.config

import com.skeleton.common.exception.slack.interceptor.LoggerInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestContextListener
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Created by KMS on 2021/05/20.
 */
@Configuration
class SlackConfig(
    val loggerInterceptor: LoggerInterceptor
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggerInterceptor)
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns("/favicon.ico", "/v3/api-docs/**")
            // .addPathPatterns("/api/**")
    }

    @Bean
    fun requestContextListener(): RequestContextListener {
        return RequestContextListener()
    }

}