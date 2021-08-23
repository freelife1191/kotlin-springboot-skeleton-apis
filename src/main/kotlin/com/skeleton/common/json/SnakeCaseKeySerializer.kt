package com.skeleton.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.common.base.CaseFormat

/**
 * Created by LYT to 2021/05/24
 */
class SnakeCaseKeySerializer: JsonSerializer<String>() {

    override fun serialize(value: String?, gen: JsonGenerator?, p2: SerializerProvider?) {
        val newValue = value?.let {
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, it)
        }

        gen?.writeFieldName(newValue)
    }
}