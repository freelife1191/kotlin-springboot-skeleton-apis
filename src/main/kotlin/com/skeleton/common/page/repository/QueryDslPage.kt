package com.skeleton.common.page.repository

import org.apache.poi.ss.formula.functions.T

/**
 * Created by LYT to 2021/09/07
 */
class QueryDslPage<T>(
    val results: MutableList<T>,
    val totalCount: Long
)