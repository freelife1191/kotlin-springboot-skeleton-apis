package com.skeleton.common.utils

import com.skeleton.common.component.excel.domain.dto.ReqExcelDownload
import org.junit.jupiter.api.Test

/**
 * Created by KMS on 2021/04/07.
 */
internal class EntityUtilsTest {

    @Test
    fun makeObject() {
        EntityUtils.makeChangeFields(ReqExcelDownload::class)
    }

    @Test
    fun makeMongoPredicate() {
        EntityUtils.makeMongoPredicate(ReqExcelDownload::class)
    }
}