package com.skeleton.common.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.skeleton.common.json.NoObjectIdSerializer
import com.skeleton.common.json.SnakeCaseKeySerializer
import org.bson.types.ObjectId
import kotlin.reflect.KClass

/**
 * Created by LYT to 2021/04/05
 */
class MapperUtils {
    companion object {
        private val objectMapper: ObjectMapper = ObjectMapper()
        // private val modelMapper: ModelMapper = ModelMapper()

        // data class 구조 deserialize 가능하도록 설정값 추가
        fun getMapper(): ObjectMapper {
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            objectMapper.registerModule(SimpleModule().addSerializer(ObjectId::class.java, NoObjectIdSerializer()))
            return getObjectMapper(objectMapper)
        }

        fun getSnakeCaseMapper(): ObjectMapper {
            val mapper = getMapper()
            mapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

            // map key convert snakeCase 되도록 수정
            mapper.registerModule(SimpleModule().addKeySerializer(String::class.java, SnakeCaseKeySerializer()))
            return getObjectMapper(mapper)
        }

        fun getCamelCaseMapper(): ObjectMapper {
            val mapper = getMapper()
            mapper.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
            return getObjectMapper(mapper)
        }

        private fun getObjectMapper(objectMapper: ObjectMapper) =
            objectMapper.registerKotlinModule()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        /**
         * SnakeCase Object 타입 변환
         * @param data Any 변환 데이터
         * @param clz KClass<T> Target Class
         * @return T
         */
        inline fun <reified T : Any> convertValueSnakeCase(data: Any, clz: KClass<T>): T =
            getSnakeCaseMapper().convertValue(data, clz.java)

        /**
         * CamelCase Object 타입 변환
         * @param data Any 변환 데이터
         * @param clz KClass<T> Target Class
         * @return T
         */
        inline fun <reified T : Any> convertValueCamelCase(data: Any, clz: KClass<T>): T =
            getCamelCaseMapper().convertValue(data, clz.java)

        /**
         * 일반 Object 타입 변환
         * @param data Any 변환 데이터
         * @param clz KClass<T> Target Class
         * @return T
         */
        inline fun <reified T : Any> convertValue(data: Any, clz: KClass<T>): T =
            getMapper().convertValue(data, clz.java)

        /**
         * List Mapping SnakeCase
         * 사용예시) MapperUtils.getListMapper(optionValueRepo.saveAll(values), OptionValueM())
         * @param source List<S>
         * @param target T
         * @return List<T>
         */
        fun <S, T : Any> mappingListSnakeCase(source: List<S>, target: KClass<T>): MutableList<T> {
            return source
                .map { getSnakeCaseMapper().convertValue(it, target.java) }
                .toMutableList()
        }

        /**
         * List Mapping CamelCase
         * 사용예시) MapperUtils.getListMapper(optionValueRepo.saveAll(values), OptionValueM())
         * @param source List<S>
         * @param target T
         * @return List<T>
         */
        fun <S, T : Any> mappingListCamelCase(source: List<S>, target: KClass<T>): MutableList<T> {
            return source
                .map { getCamelCaseMapper().convertValue(it, target.java) }
                .toMutableList()
        }
    }
}