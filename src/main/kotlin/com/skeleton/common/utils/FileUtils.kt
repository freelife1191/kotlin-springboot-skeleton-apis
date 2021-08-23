package com.skeleton.common.utils

import java.io.File

/**
 * 파일처리 유틸
 * Created by KMS on 2021/08/03.
 */
class FileUtils {

    companion object {
        /**
         * delete
         * @param files
         */
        fun deleteFile(vararg files: File) {
            for (file in files) {
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }
}