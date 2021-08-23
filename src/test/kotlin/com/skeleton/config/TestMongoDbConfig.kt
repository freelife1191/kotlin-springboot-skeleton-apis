package com.skeleton.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

/**
 * Created by LYT to 2021/04/09
 */
@TestConfiguration
class TestMongoDbConfig {
    @Value("\${spring.data.mongodb.uri}")
    private lateinit var connectionString : String

    @Bean
    fun mongoDatabaseFactory() : MongoDatabaseFactory = SimpleMongoClientDatabaseFactory(connectionString)
    @Bean
    fun mongoTemplate() : MongoTemplate = MongoTemplate(mongoDatabaseFactory())
}
