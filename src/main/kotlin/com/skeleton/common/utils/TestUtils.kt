package com.skeleton.common.utils

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.jvm.Throws

/**
 * Created by KMS on 2021/05/07.
 */
class TestUtils {
    companion object {
        /**
         * Get DTO를 TestCode params 에서 사용할 수 있게 MultiValueMap Type으로 Mapping
         * @param dto T
         * @return MultiValueMap<String, String>
         */
        @Throws(Exception::class)
        inline fun <reified T: Any> paramMapper(dto: T): MultiValueMap<String, String> {
            val paramMap: MutableMap<String, String> =
                MapperUtils.convertValueSnakeCase(dto, MutableMap::class) as MutableMap<String, String>
            /*
            dto.javaClass.kotlin.memberProperties.forEach {
                val data = it.getter.call(dto)
                if(data is Map<*, *>)
                    paramMap[it.name] = JsonUtils.toMapperJson(data)
                // println("## ${it.name}, type = ${it.getter.call(dto) }")
            }
            */
            val multiValueMap: MultiValueMap<String, String> = LinkedMultiValueMap()
            multiValueMap.setAll(
                paramMap.keys
                    .filter { paramMap[it] != null }
                    .associateWith { paramMap[it].toString() }
            )
            return multiValueMap
        }
    }
}