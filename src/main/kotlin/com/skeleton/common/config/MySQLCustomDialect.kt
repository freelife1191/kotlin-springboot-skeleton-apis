package com.skeleton.common.config

import org.hibernate.dialect.MySQL57Dialect
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

/**
 * Created by KMS on 2021/05/04.
 */
class MySQLCustomDialect: MySQL57Dialect() {
    init {
        registerFunction(
            "GROUP_CONCAT",
            StandardSQLFunction("group_concat", StandardBasicTypes.STRING)
        )
    }
}