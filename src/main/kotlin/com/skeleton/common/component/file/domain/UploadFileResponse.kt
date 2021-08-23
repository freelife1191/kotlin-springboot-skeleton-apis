package com.skeleton.common.component.file.domain

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

/**
 * 업로드 응답 객체
 * Created by KMS on 2021/04/18.
 */
class UploadFileResponse(
    var fileName: String? = null,
    var fileSize: Long? = null,
    var fileUrl: String? = null,
    var savePath: String? = null,
    var fileType: String? = null,

) {
    constructor(multipartFile: MultipartFile, responseUrl: String, savePath: String) : this() {
        this.fileName = multipartFile.originalFilename.toString()
        this.fileSize = multipartFile.size
        this.fileUrl = responseUrl
        this.savePath = savePath
        this.fileType = multipartFile.contentType.toString()
    }

    constructor(resource: Resource, responseUrl: String, savePath: String) : this() {
        this.fileName = resource.filename
        this.fileUrl = responseUrl
        this.savePath = savePath
    }

    override fun toString(): String {
        return "UploadFileResponse(fileName=$fileName, fileSize=$fileSize, fileUrl=$fileUrl, savePath=$savePath, fileType=$fileType)"
    }

}