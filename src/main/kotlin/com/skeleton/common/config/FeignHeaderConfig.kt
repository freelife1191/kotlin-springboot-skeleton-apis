package com.skeleton.common.config

import com.skeleton.common.component.security.domain.constant.TempToken
import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by LYT to 2021/06/08
 *
 * @Configuration 적용 시 모든 feign-client에 적용
 *   - 내부 API 통신 용 jwt 발급
 */
@Configuration
class FeignHeaderConfig {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            // expire : 2221-06-08T10:36:23.995231
            it.header("Authorization", TempToken.JWT)
        }
    }
}