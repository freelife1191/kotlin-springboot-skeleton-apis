package com.skeleton.common.utils

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Created by LYT to 2021/04/21
 */
infix fun LocalDate.by(type: DateTypeEnum): LocalDateTime =
    when(type) {
        DateTypeEnum.START_DATE -> this.atTime(0,0,0)
        DateTypeEnum.END_DATE -> this.atTime(23,59,59)
    }