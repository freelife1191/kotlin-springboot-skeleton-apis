package com.skeleton.common.component.tutorial

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by KMS on 2021/08/11.
 */
@Component
class Greeting (
    @Value("\${greeting.message}")
    val message: String? = null
)