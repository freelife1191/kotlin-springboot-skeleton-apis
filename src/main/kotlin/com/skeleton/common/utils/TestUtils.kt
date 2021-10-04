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
        fun <T> paramMapper(dto: T): MultiValueMap<String, String> {
            val paramMap: MutableMap<String, Any> =
                MapperUtils.convertValueSnakeCase(dto!!, Map::class) as MutableMap<String, Any>
            val multiValueMap: MultiValueMap<String, String> = LinkedMultiValueMap()
            // List타입의 데이터의 경우 별도로 add 처리로 데이터를 담는다
            paramMap.keys
                .filter { paramMap[it] is ArrayList<*> }
                .forEach {
                    (paramMap[it] as ArrayList<*>).forEach {data ->
                        multiValueMap.add(it, data.toString()) }
                    paramMap.remove(it)} // 성능향상을 위해 처리한 데이터는 삭제처리
            //List타입의 데이터가 아니면 그대로 setAll 처리 한다
            multiValueMap.setAll(
                paramMap.keys
                    .filter { paramMap[it] !is ArrayList<*> && paramMap[it] != null }
                    .associateWith { paramMap[it].toString() })
            return multiValueMap
        }
    }
}