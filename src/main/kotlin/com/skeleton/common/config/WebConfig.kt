package com.skeleton.common.config

import com.skeleton.common.exception.slack.filter.HttpLoggingFilter
import feign.okhttp.OkHttpClient
import org.apache.catalina.connector.Connector
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.filter.AbstractRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.*
import java.time.Duration


/**
 * Created by KMS on 2021/03/08.
 */
@Configuration
class WebConfig: WebMvcConfigurer {

    /**
     * CORS 설정 모두허용
     * @param registry
     */
    // override fun addCorsMappings(registry: CorsRegistry) {
    //     registry.addMapping("/**")
    //         .allowedOrigins("*")
    //         .allowedMethods(
    //             HttpMethod.POST.name,
    //             HttpMethod.GET.name,
    //             HttpMethod.PUT.name,
    //             HttpMethod.PATCH.name,
    //             HttpMethod.DELETE.name,
    //             HttpMethod.OPTIONS.name,
    //             HttpMethod.HEAD.name,
    //             HttpMethod.TRACE.name
    //         )
    //         .allowCredentials(false)
    //         .allowedHeaders("*")
    //         .maxAge((60 * 60).toLong()) // 1시간
    // }


    /*
    @Bean
    fun restTemplateCustomizer(restTemplateBuilder: RestTemplateBuilder): RestTemplate? {
        return restTemplateBuilder
            .requestFactory(Supplier<ClientHttpRequestFactory> { HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()) }) //.additionalInterceptors(new RestTemplateClientHttpRequestInterceptor)
            //.errorHandler(new RestTemplateErrorHandler())
            .setConnectTimeout(Duration.ofSeconds(30)) //접속유지 30초
            .setReadTimeout(Duration.ofSeconds(300)) //응답대기 300초
            .build()
    }
    */
    @LoadBalanced
    @Bean
    fun restTemplate(): RestTemplate? {
        val restTemplateBuilder = RestTemplateBuilder()
        return restTemplateBuilder
            .requestFactory {
                HttpComponentsClientHttpRequestFactory()
            } //.additionalInterceptors(new RestTemplateClientHttpRequestInterceptor)
            //.errorHandler(new RestTemplateErrorHandler())
            .setConnectTimeout(Duration.ofSeconds(30)) //접속유지 30초
            .setReadTimeout(Duration.ofSeconds(300)) //응답대기 300초
            .build()
    }

    @Bean
    fun client(): OkHttpClient? {
        return OkHttpClient()
    }

    @Bean
    fun tomcatServletWebServerFactory(): TomcatServletWebServerFactory? {
        return object : TomcatServletWebServerFactory() {
            override fun customizeConnector(connector: Connector) {
                super.customizeConnector(connector)
                connector.parseBodyMethods = "POST,PUT,PATCH,DELETE"
            }
        }
    }

    @Bean
    fun requestLoggingFilter(): AbstractRequestLoggingFilter {
        val filter = HttpLoggingFilter()
        filter.setIncludeClientInfo(true)
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        return filter
    }
}

    /**
     * Swagger UI 리소스 설정
     * https://github.com/springfox/springfox-demos/blob/master/boot-swagger/src/main/java/springfoxdemo/boot/swagger/SwaggerUiWebMvcConfigurer.java
     * https://github.com/springfox/springfox-demos/blob/master/spring-java-swagger/src/main/java/springfoxdemo/java/swagger/SpringConfig.java
     * @param registry
     */
/*
     override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
            .resourceChain(false)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/swagger-ui/")
            .setViewName("forward:" + "/swagger-ui/index.html")
        registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/index.html")
    }
}   */