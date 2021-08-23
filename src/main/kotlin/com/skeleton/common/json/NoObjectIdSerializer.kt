package com.skeleton.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bson.types.ObjectId

/**
 * Created by KMS on 2021/05/13.
 */
class NoObjectIdSerializer: JsonSerializer<ObjectId>() {

    override fun serialize(value: ObjectId?, jgen: JsonGenerator?, provider: SerializerProvider?) {
        if(value == null){
            jgen?.writeNull()
        }else{
            jgen?.writeString(value.toString())
        }
    }
}