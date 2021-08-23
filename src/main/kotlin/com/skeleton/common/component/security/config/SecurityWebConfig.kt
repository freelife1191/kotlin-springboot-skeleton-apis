package com.skeleton.common.component.security.config

import com.skeleton.common.component.security.interceptor.SecurityInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Created by LYT to 2021/06/23
 */
@Component
class SecurityWebConfig(
    val interceptor: SecurityInterceptor
): WebMvcConfigurer {

    /**
     * auth token interceptor
     * @param registry InterceptorRegistry
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor)
            .addPathPatterns("/api/v1/**")
    }
}