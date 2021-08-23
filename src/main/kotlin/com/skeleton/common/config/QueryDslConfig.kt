package com.skeleton.common.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


/**
* Created by KMS on 2021/02/24.
*/
@Configuration
class QueryDslConfig (@PersistenceContext val entityManager: EntityManager) {
   @Bean
   fun jpqQueryFactory() = JPAQueryFactory(entityManager)
}