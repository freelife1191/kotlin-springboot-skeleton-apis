package com.skeleton.common.utils

import org.junit.jupiter.api.Test
import java.nio.file.Path

/**
 * Created by KMS on 2021/04/15.
 */
class PathUtilsTest {

    @Test
    fun `PathUtils getPath 테스트`() {
        println("## getPath = ${PathUtils.getPath(Path.of("test","resorces")).toString()}")
    }

    @Test
    fun `PathUtils null 테스트`() {
        val path: Path? = null
        println("## getPath = ${PathUtils.getPath(path)}")
    }

    @Test
    fun `PathUtils String 테스트`() {
        println(PathUtils.getPath("test\\resource\\abc"))
    }

    @Test
    fun `PathUtils String null 테스트`() {
        val str: String? = null
        println("## getPath = ${PathUtils.getPath(str)}")
    }
}