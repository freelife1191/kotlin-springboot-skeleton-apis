package com.skeleton.common.component.file.service

import com.skeleton.common.component.file.domain.S3FileInfo
import com.skeleton.common.component.file.domain.S3FileOption
import com.skeleton.common.component.file.domain.UploadFileResponse
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by KMS on 2021/04/16.
 */
@Service
class FileService(
    var s3FileService: S3FileService
) {
    val log: Logger = LogManager.getLogger()

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String? = null

    @Value("\${cloud.aws.s3.domain}")
    private val s3domain: String? = null

    @Value("\${cloud.aws.s3.download}")
    private val downloadDomain: String? = null

    /**
     * 파일 업로드
     * 파일 업로드 시 serviceName/category/type/contactId/UUID 형식으로 Path를 생성해서 전달
     * @param multipartFile
     * @return
     */
    fun upload(multipartFile: MultipartFile, path: Path, fileOption: S3FileOption = S3FileOption()): UploadFileResponse {
        val savePath = Paths.get(path.toString(), fileOption.getFileName(fileOption, multipartFile))
        return UploadFileResponse(multipartFile, s3FileService.upload(multipartFile, savePath).replaceFirst(s3domain!!, downloadDomain!!), savePath.toString())
    }

    /**
     * 다중 파일 업로드
     * @param multipartFiles
     * @return
     */
    fun upload(multipartFiles: Array<MultipartFile>, path: Path, fileOption: S3FileOption = S3FileOption()): List<UploadFileResponse> {
        val resultList: MutableList<UploadFileResponse> = mutableListOf()

        multipartFiles.forEach {
            val savePath = Paths.get(path.toString(), fileOption.getFileName(fileOption, it))
            resultList.add(UploadFileResponse(it, s3FileService.upload(it, savePath).replaceFirst(s3domain!!, downloadDomain!!), savePath.toString()))
        }
        return resultList
    }

    /**
     * Resource 파일 업로드
     * 파일 업로드 시 serviceName/category/type/contractId/UUID 형식으로 Path를 생성해서 전달
     * @param resource
     * @return
     */
    fun upload(resource: Resource, path: Path): UploadFileResponse {
        val savePath = Paths.get(path.toString(), S3FileOption().getFileName(S3FileOption(), null))
        return UploadFileResponse(resource, s3FileService.upload(resource, savePath).replaceFirst(s3domain!!, downloadDomain!!), savePath.toString())
    }

    /**
     * Resource 다중 파일 업로드
     * 파일 업로드 시 serviceName/category/type/contractId/UUID 형식으로 Path를 생성해서 전달
     * @param resources
     * @return
     */
    fun upload(resources: List<Resource>, path: Path): List<UploadFileResponse> {
        val resultList: MutableList<UploadFileResponse> = mutableListOf()
        resources.forEach {
            val savePath = Paths.get(path.toString(), S3FileOption().getFileName(S3FileOption(), null))
            resultList.add(UploadFileResponse(it, s3FileService.upload(it, savePath).replaceFirst(s3domain!!, downloadDomain!!), savePath.toString()))
        }
        return resultList
    }

    /**
     * S3 파일 이동
     * @param path
     * @param movePath
     */
    fun move(path: Path, movePath: Path): ResponseEntity<Any> {
        s3FileService.move(path, movePath)
        return ResponseEntity.ok().build()
    }

    /**
     * 파일 다운로드 S3에서 파일 다운로드
     * 파일 다운로드 예외처리시 유의 사항
     * FileDownloadFailException 임의 예외 발생시 Transactional 에 의해 롤백되지 않도록 noRollbackFor 처리 필요
     * @param path
     * @return
     */
    fun download(path: Path): Resource? {
        return s3FileService.download(path, bucket)
    }

    /**
     * 바이트형식 파일 다운로드
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    fun download(path: Path, fileName: String): ResponseEntity<ByteArray> {
        return s3FileService.byteDownload(path, fileName)
    }

    /**
     * 단일 파일 삭제
     * 파일이 존재 하지 않는 경우에는 에러 메세지만 출력 그외 에러 발생시에는 에러발생시킴
     * @param path
     * @return
     */
    fun delete(path: Path): ResponseEntity<Any> {
        s3FileService.delete(path)
        return ResponseEntity.ok().build()
    }

    /**
     * 다중 파일 삭제
     * 파일이 존재 하지 않는 경우에는 에러 메세지만 출력후 다음 건 실행 그외 에러 발생시에는 에러발생시켜 처리를 중단함
     * @param fileList
     * @return
     */
    fun delete(path: Path, fileList: List<Path>): ResponseEntity<Any> {
        s3FileService.delete(path, fileList)
        return ResponseEntity.ok().build()
    }

    /**
     * 다중 파일 삭제
     * 파일이 존재 하지 않는 경우에는 에러 메세지만 출력후 다음 건 실행 그외 에러 발생시에는 에러발생시켜 처리를 중단함
     * @param fileList
     * @return
     */
    fun deleteString(fileList: List<String>): ResponseEntity<Any> {
        return delete(fileList.map { Paths.get(it) }.toList())
    }

    /**
     * 다중 파일 삭제
     * 파일이 존재 하지 않는 경우에는 에러 메세지만 출력후 다음 건 실행 그외 에러 발생시에는 에러발생시켜 처리를 중단함
     * @param fileList
     * @return
     */
    fun delete(fileList: List<Path>): ResponseEntity<Any> {
        s3FileService.delete(fileList)
        return ResponseEntity.ok().build()
    }

    /**
     * 특정 경로의 파일 리스트 조회
     * @param fileList
     * @return
     */
    fun getFileList(fileList: List<Path>): List<Path> {
        return fileList
    }

    /**
     * 특정 경로의 파일 리스트 조회
     * @param prefix
     * @return
     */
    fun getFileList(prefix: Path): List<S3FileInfo> {
        return s3FileService.getFileList("/", prefix)
    }
}