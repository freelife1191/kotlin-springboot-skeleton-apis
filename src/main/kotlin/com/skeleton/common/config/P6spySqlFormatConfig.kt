package com.skeleton.common.config

import com.p6spy.engine.common.P6Util
import com.p6spy.engine.logging.Category
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle
import java.util.*

/**
 * Created by LYT to 2021/04/06
 */
class P6spySqlFormatConfig: MessageFormattingStrategy {
    companion object {
        private const val SQL_CREATE = "create"
        private const val SQL_ALTER = "alter"
        private const val SQL_COMMENT = "comment"
    }

    // apply sql format
    override fun formatMessage(
        connectionId: Int,
        now: String?,
        elapsed: Long,
        category: String?,
        prepared: String?,
        sql: String?,
        url: String?
    ): String {
        val singleLine = P6Util.singleLine(prepared)
        val formatSql = formatSql(category, sql)

        return "$now | $elapsed ms | $category |connection $connectionId | $singleLine $formatSql"
    }


    private fun formatSql(category: String?, sql: String?): String? {
        var resultSql = "";
        if (sql == null || sql.trim() == "")
            return resultSql

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.name == category) {
            val tempSql = sql.trim().toLowerCase(Locale.ROOT)
            resultSql =
                if (tempSql.startsWith(SQL_CREATE) || tempSql.startsWith(SQL_ALTER) || tempSql.startsWith(SQL_COMMENT))
                    FormatStyle.DDL.formatter.format(sql)
                else
                    FormatStyle.BASIC.formatter.format(sql)
            resultSql = "|\nHeFormatSql(P6Spy sql,Hibernate format):$resultSql"
        }
        return resultSql
    }
}