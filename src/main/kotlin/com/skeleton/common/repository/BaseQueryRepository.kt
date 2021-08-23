package com.skeleton.common.repository

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Created by KMS on 2021/04/08.
 */
interface BaseQueryRepository<T, E> {
    fun selectPageableByCondition(searchDTO: T, pageable: Pageable) : PageImpl<E>
}