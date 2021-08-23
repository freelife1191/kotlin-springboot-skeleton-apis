package com.skeleton.common.component.file.domain

import com.fasterxml.uuid.Generators
import com.skeleton.common.component.file.enum.S3FileNameType
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

/**
 * 파일처리를 위한 옵션 객체
 * Created by KMS on 2021/04/16.
 */
data class S3FileOption(
    val customfileName: String? = null,
    var nameTypeType: S3FileNameType? = S3FileNameType.UUID //기본값은 UUID 파일명
) {

    /**
     * S3 File Option에 따른 파일명 적용
     */
    fun getFileName(fileOption: S3FileOption, file: Any? = null): String {
        return when(fileOption.nameTypeType) {
            S3FileNameType.UUID -> Generators.timeBasedGenerator().generate().toString()
            S3FileNameType.CUSTOM -> customfileName ?: throw IllegalArgumentException("Empty Custom FileName")
            else -> {
                return when (file) {
                    is MultipartFile -> file.originalFilename ?: throw IllegalArgumentException("is Empty MultipartFile originalFileName")
                    is Resource -> file.filename ?: throw IllegalArgumentException("is Empty Resource FileName")
                    is String -> file
                    else -> throw IllegalArgumentException("is Empty FileName")
                }
            }
        }
    }
}