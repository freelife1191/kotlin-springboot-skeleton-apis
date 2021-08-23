package com.skeleton.common.config

import org.springframework.data.auditing.DateTimeProvider
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Created by KMS on 2021/06/14.
 */
@Component("dateTimeProvider")
class CustomDateTimeProvider: DateTimeProvider {
    override fun getNow(): Optional<TemporalAccessor> {
        return Optional.of(ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.of("Asia/Seoul")))
    }
}