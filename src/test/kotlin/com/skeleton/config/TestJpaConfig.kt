package com.skeleton.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created by LYT to 2021/04/05
 */
@TestConfiguration
class TestJpaConfig(@PersistenceContext val entityManager: EntityManager) {
    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(entityManager)
}