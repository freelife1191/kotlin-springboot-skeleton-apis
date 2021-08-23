package com.skeleton.common.component.file.domain

import java.time.LocalDateTime

/**
 * S3 파일 조회 객체
 * Created by KMS on 2021/04/16.
 */
data class S3FileInfo(
    val bucketName: String? = null,
    val key: String? = null,
    val size: Long = 0,
    val lastModified: LocalDateTime? = null,
    val storageClass: String? = null
)