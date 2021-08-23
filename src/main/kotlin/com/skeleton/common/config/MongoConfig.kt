package com.skeleton.common.config

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.Expressions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.convert.DbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.core.query.Update
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KProperty


@Configuration
// @EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EnableMongoAuditing//(dateTimeProviderRef = "dateTimeProvider")
// https://stackoverflow.com/questions/49170180/createdby-and-lastmodifieddate-are-no-longer-working-with-zoneddatetime
// https://stackoverflow.com/questions/43236431/register-a-new-date-converter-auditable-in-spring-data-mongodb-for-zoneddatetime
class MongoConfig {

//    @Bean
//    fun transactionManager(dbFactory: MongoDatabaseFactory): MongoTransactionManager =
//        MongoTransactionManager(dbFactory)

    /*
    @Bean // Makes ZonedDateTime compatible with auditing fields
    fun auditingDateTimeProvider()= DateTimeProvider {
        of(ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.of("Asia/Seoul")))
    }
    */

    @Bean
    fun mongoConverter(
        mongoFactory: MongoDatabaseFactory?,
        mongoMappingContext: MongoMappingContext?,
    ): MappingMongoConverter? {
        val dbRefResolver: DbRefResolver = DefaultDbRefResolver(mongoFactory!!)
        val mongoConverter = MappingMongoConverter(dbRefResolver, mongoMappingContext!!)
        mongoConverter.setMapKeyDotReplacement("-DOT")
        return mongoConverter
    }

    /**
     * MongoDB 날짜데이터 입력출력시 한국시간으로 변환
     * @return MongoCustomConversions
     */
    @Bean
    fun customConversions(): MongoCustomConversions {
        val converters: MutableList<Converter<*, *>> = mutableListOf()
        converters.add(DateToZonedDateTimeConverter())
        converters.add(ZonedDateTimeToDateConverter())
        converters.add(LocalDateTimeToDateConverter())
        converters.add(DateToLocalDateTimeConverter())
        return MongoCustomConversions(converters)
    }

    class DateToZonedDateTimeConverter : Converter<Date, ZonedDateTime> {
        override fun convert(date: Date): ZonedDateTime {
            return date.toInstant().atZone(ZoneOffset.UTC)
        }
    }

    class ZonedDateTimeToDateConverter : Converter<ZonedDateTime, Date> {
        override fun convert(zonedDateTime: ZonedDateTime): Date {
            return Date.from(zonedDateTime.toInstant())
        }
    }

    /**
     * MongoDB에 등록시 날짜데이터를 한국시간으로 변환
     */
    class LocalDateTimeToDateConverter : Converter<LocalDateTime, Date> {
        override fun convert(source: LocalDateTime): Date {
            return Date.from(source.toInstant(ZoneOffset.UTC))
        }
    }

    /**
     * MongoDB에서 조회해오는 날짜데이터를 UTC로 변환
     */
    class DateToLocalDateTimeConverter : Converter<Date, LocalDateTime> {
        override fun convert(source: Date): LocalDateTime {
            return source.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
        }
    }
}

// Querydsl 에서의 text 인덱스에 대한 FTS 를 위한 TextCriteria.toPredicate() 확장 함수 작성
fun TextCriteria.toPredicate(documentType: Any): Predicate {

    val path: Path<Any>? = ExpressionUtils.path(documentType::class.java, "\$text")
    val value = Expressions.constant(this.criteriaObject["\$text"])

    return ExpressionUtils.predicate(Ops.EQ, path, value)
}

// Type-Safe로 작동하는 Update.set() 확장 함수 작성
fun Update.set(property: KProperty<*>, value: Any?): Update {

    return set(property.name, value)
}

// Type-Safe로 작동하는 Update.inc() 확장 함수 작성
fun Update.inc(property: KProperty<*>, value: Number): Update {

    return inc(property.name, value)
}