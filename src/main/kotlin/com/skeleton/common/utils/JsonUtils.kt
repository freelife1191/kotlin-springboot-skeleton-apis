package com.skeleton.common.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import org.apache.commons.text.StringEscapeUtils
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * The type Json utils.
 *
 * Created by KMS on 2021/05/20.
 */
class JsonUtils {

    private var gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setLenient()
        .create()
    private var mapper: ObjectMapper = ObjectMapper()

    companion object {

        /**
         * Gets instance.
         *
         * @return the instance
         */
        private fun getInstance() = JsonUtils()

        fun getGson() = getInstance().gson

        fun getObjectMapper() = getInstance().mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

        /**
         * To json string.
         *
         * @param any the object
         * @return the string
         */
        fun toJson(any: Any): String {
            return StringEscapeUtils.unescapeJava(getGson().toJson(any))
        }

        /**
         * From json t.
         *
         * @param <T>     the type parameter
         * @param jsonStr the json str
         * @param cls     the cls
         * @return the t
        </T> */
        fun <T> fromJson(jsonStr: String, cls: Class<T>?): T {
            return getGson().fromJson(jsonStr, cls)
        }

        /**
         * From json json element.
         *
         * @param json the json
         * @return the json element
         * @throws Exception the exception
         */
        fun fromJson(json: String): JsonElement? {
            return getGson().toJsonTree(json)
        }

        /**
         * From json t.
         *
         * @param <T>     the type parameter
         * @param jsonStr the json str
         * @param type    the type
         * @return the t
        </T> */
        @Throws(JsonSyntaxException::class)
        private fun <T : Collection<Any>> fromJson(jsonStr: String, type: Type?): T {
            return getGson().fromJson(jsonStr, type)
        }

        /**
         * To pretty json string.
         *
         * @param json the json
         * @return the string
         */
        fun toPrettyJson(json: String?): String? {
            if (json == null) return null
            return StringEscapeUtils.unescapeJava(getGson().toJson(fromJson(json, Any::class.java)))
        }

        /**
         * To ObjectMapper pretty json string
         * @param object
         * @return
         */
        @Throws(JsonProcessingException::class)
        fun toMapperJson(any: Any): String = getObjectMapper().writeValueAsString(any)

        /**
         * To ObjectMapper pretty json string
         * @param any
         * @return
         */
        @Throws(JsonProcessingException::class)
        fun toMapperPrettyJson(any: Any?): String {
            return try {
                getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(any)
            } catch (e: Exception) {
                println(e.message)
                ""
            }
        }

        /**
         * To ObjectMapper Mapping json string
         * @param obj JSON String
         * @param clz 변환할 오브젝트 클래스
         * @param <T>
         * @return
        </T> */
        @Throws(JsonProcessingException::class)
        fun <T: Any> toMapperObject(obj: String, clz: KClass<T>): T = getObjectMapper().readValue(obj, clz.java)
        /**
         * To ObjectMapper Mapping json string
         * @param obj JSON String
         * @param type 변환할 오브젝트 클래스
         * @param <T>
         * List 와 같은 형태는 TypeReference 형으로 처리
         * new TypeReference<List></List><Object>() {}
         * @return
        </Object></T> */
        @Throws(JsonProcessingException::class)
        fun <T> toMapperObject(obj: String, type: TypeReference<T>): T = getObjectMapper().readValue(obj, type)
    }
}