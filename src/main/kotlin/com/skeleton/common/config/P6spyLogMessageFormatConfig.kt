package com.skeleton.common.config

import com.p6spy.engine.spy.P6SpyOptions
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

/**
 * Created by LYT to 2021/04/06
 */
@Configuration
class P6spyLogMessageFormatConfig {
    @PostConstruct
    fun setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat = P6spySqlFormatConfig::class.java.name // pretty

        // exclude sql pattern
//        P6LogOptions.getActiveInstance().setFilter(true);
//        P6LogOptions.getActiveInstance().setExclude("store");
    }
}