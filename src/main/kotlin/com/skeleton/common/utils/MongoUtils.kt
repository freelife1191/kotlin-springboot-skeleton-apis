package com.skeleton.common.utils

import org.apache.commons.lang3.BooleanUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Update
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.reflect.full.memberProperties

/**
 * Created by KMS on 2021/05/12.
 */
class MongoUtils {
    companion object {

        val log: Logger = LoggerFactory.getLogger(this::class.java)

        /**
         * MongoDB에 현재시간 조정반영
         * @return LocalDateTime
         */
        fun nowDataTime(): LocalDateTime =
            ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()

        /**
         * UPDATE 시 값이 null 이 아닌 데이터만 업데이트 처리
         * @param req T RequestDTO 객체
         * @param ignoreKeys List<String> 무시할 키값 리스트
         * @return Update
         */
        inline fun <reified T: Any> update(req: T, ignoreKeys: List<String> = listOf()): Update {

            val entity = MapperUtils.getSnakeCaseMapper().convertValue(req, T::class.java)

            val update = Update().set("updatedAt", LocalDateTime.now())
            entity::class.memberProperties
                .filterNot { it.name in ignoreKeys }
                .filter { it.getter.call(entity) != null }
                .forEach {
                    if(it.name == "deleteFlag" && BooleanUtils.toBoolean(it.getter.call(entity).toString()))
                        update.set("deletedAt", LocalDateTime.now())
                   else if(it.name == "deleteFlag" && !BooleanUtils.toBoolean(it.getter.call(entity).toString()))
                        update.unset("deletedAt")
                    // println("## ${it.name} =  ${it.getter.call(entity)}")
                    log.debug("## Update Set ${it.name}, ${it.getter.call(entity)}")
                    update.set(it.name, it.getter.call(entity))
                }

            return update
        }
    }
}