package com.skeleton.common.component.tutorial

import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by KMS on 2021/08/11.
 */
@RestController
@RequestMapping("/tutorial/health_check")
@Tag(name = "[Tutorial] Tutorial", description = "TutorialController")
class TutorialController(
    val env: Environment,
    val greeting: Greeting
) {

    @Operation(summary = "서버 상태 체크")
    @GetMapping("/health_check")
    @Timed(value = "tutorial.status", longTask = true)
    fun status(): String =
            "It's Woriking in User Service" +
            ", port(local.server.port)=${env.getProperty("local.server.port")}" +
            ", port(server.port)=${env.getProperty("server.port")}" +
            ", token secret=${env.getProperty("token.secret")}" +
            ", token expiration time=${env.getProperty("token.expiration_time")}"

    @Operation(summary = "웰컴")
    @GetMapping("/welcome")
    @Timed(value = "tutorial.welcome", longTask = true)
    fun welcome(): String = greeting.message ?: ""

}