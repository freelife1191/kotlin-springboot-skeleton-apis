package com.skeleton.common.domain.entity

import org.springframework.data.mongodb.core.mapping.Field

/**
 * Created by LYT to 2021/04/12
 */
class CodebookInfo(
    @Field("code_value")
    val codeValue: String,

    @Field("code_name")
    val codeName: String,

    @Field("sort_no")
    val sortNo: Int,

    @Field("is_use")
    val isUse: Boolean,
)