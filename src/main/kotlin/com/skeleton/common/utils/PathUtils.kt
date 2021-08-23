package com.skeleton.common.utils

import org.apache.commons.lang3.StringUtils
import java.nio.file.Path

/**
 * Created by KMS on 2021/04/15.
 */
class PathUtils {
    companion object {
        /**
         * 잘못된 경로 정정 후 리턴
         * @param path
         * @return
         */
        fun getPath(path: Path?): String? {
            return path?.let { getPath(it.toString()) }
        }

        /**
         * 잘못된 경로 정정 후 리턴
         * @param path
         * @return
         */
        fun getPath(path: String?): String? {
            return StringUtils.isEmpty(path).let { StringUtils.replace(path, "\\", "/") }
        }
    }
}