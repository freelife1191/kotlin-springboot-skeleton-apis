package com.skeleton.common.page.repository

import com.querydsl.core.types.dsl.PathBuilderFactory
import com.querydsl.jpa.JPQLQuery
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.Querydsl
import javax.persistence.EntityManager
import kotlin.reflect.KClass

/**
 * Created by LYT to 2021/04/01
 * QuerydslRepositorySupport 참고하여 생성
 */
open class QuerydslPageAndSortRepository(
    private val entityManager: EntityManager,
    private val clazz: KClass<*>
) {

    fun getQuerydsl(): Querydsl {
        val builder = PathBuilderFactory().create(clazz.java)
        return Querydsl(entityManager, builder)
    }

    fun <T> getPageImpl(pageable: Pageable, query: JPQLQuery<T>): PageImpl<T> {
        val totalCount = query.fetchCount()

        val results = getQuerydsl()
            .applyPagination(pageable, query)
            .fetch()

        return PageImpl(results, pageable, totalCount)
    }
}