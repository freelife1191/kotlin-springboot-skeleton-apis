package com.skeleton.common.domain.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * Created by LYT to 2021/04/12
 */
@Document(collection = "codebook")
class Codebook(
    @Field("code_type")
    var codeType: String,

    @Field("code_key")
    var codeKey: String,

    @Field("code_info")
    var codeInfo: Array<CodebookInfo> = arrayOf(),
//    var codeInfo: MutableMap<String, Any>
)