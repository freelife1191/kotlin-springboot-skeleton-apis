package com.skeleton.common.component.security.utils

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Created by LYT to 2021/06/08
 */
@Component
class SystemUtils(
    private val env: Environment
) {
    val local = "local"
    val my = "my"
    val dev = "dev"
    val prod = "prod"
    val stage = "stage"

    fun isLocal() = getActiveProfiles().contains(local)

    fun isMy() = getActiveProfiles().contains(my)

    fun isDev() = getActiveProfiles().contains(dev)

    fun isProd() = getActiveProfiles().contains(prod)

    fun isStage() = getActiveProfiles().contains(stage)

    fun getActiveProfiles() = env.activeProfiles
}