package com.skeleton.common.component.file.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.*
import com.skeleton.common.component.file.domain.S3FileInfo
import com.skeleton.common.utils.PathUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLEncoder
import java.nio.file.AccessDeniedException
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZoneId

/**
 * Created by KMS on 2021/04/15.
 */
@Component
class S3FileService(
    private var amazonS3Client: AmazonS3Client
){

    val log: Logger = LogManager.getLogger()
    
    @Value("\${cloud.aws.s3.bucket}")
    val bucket: String? = null


    /**
     * 컨트롤러에서 호출 MultipartFile -> File 전환
     * @param multipartFile 업로드할 파일
     * @param path S3에 생성될 디렉토리명
     * @return
     * @throws IOException
     */
    fun upload(multipartFile: MultipartFile, path: Path): String {
        // println("## path = $path, fileName = ${fileOption.getFileName(fileOption, multipartFile)}")
        return s3Upload(multipartFile, Paths.get(path.toString()))
    }

    /**
     * 컨트롤러에서 호출 MultipartFile -> File 전환 다중 처리
     * @param multipartFiles
     * @param path
     * @return
     */
    fun upload(multipartFiles: Array<MultipartFile>, path: Path): List<String> {
        val resultList: MutableList<String> = arrayListOf()
        for (multipartFile in multipartFiles) resultList.add(upload(multipartFile, path))
        return resultList
    }

    /**
     * Resource 파일 업로드
     * @param resource
     * @param path
     * @return
     */
    fun upload(resource: Resource, path: Path): String {
        log.info("[S3] Upload File = {}, path = {}", resource.filename, PathUtils.getPath(path))
        //업로드 후 파일 S3 업로드 상세경로 리턴
        return putS3(resource, path)
    }


    /**
     * Resource 다중 파일 업로드
     * @param resources
     * @param path
     * @return
     */
    fun upload(resources: List<Resource>, path: Path): List<String> {
        val resultList: MutableList<String> = arrayListOf()
        for (resource in resources) resultList.add(upload(resource, path))
        return resultList
    }

    /**
     * S3 업로드 정보 셋팅 및 업로드
     * @param s3UploadFile 업로드할 파일
     * @param path S3에 생성될 디렉토리명
     * @return
     */
    fun s3Upload(s3UploadFile: MultipartFile, path: Path): String {
        //S3에 저장될 경로와 파일명 지정
        // String s3UploadfileName = path + "/" + s3UploadFile.getName();
        log.info("[S3] Upload File = {}, path = {}", s3UploadFile.name, PathUtils.getPath(path))
        //업로드 후 파일 S3 업로드 상세경로 리턴
        // removeNewFile(s3UploadFile);
        return putS3(s3UploadFile, path)
    }

    /**
     * S3에 파일 최종 업로드 처리
     * @param resource 업로드할 파일
     * @param path S3에 저장될 상세 경로(경로 + 파일명)
     * @return
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class, IOException::class)
    fun putS3(resource: Resource, path: Path): String {
        try {
            val metadata = ObjectMetadata()
            metadata.contentLength = resource.contentLength()
            amazonS3Client.putObject(
                PutObjectRequest(bucket, PathUtils.getPath(path), resource.inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("S3 쓰기 권한이 없습니다 S3계정의 권한을 확인하세요")
            if (e.statusCode == HttpStatus.NOT_FOUND.value() && e.errorCode == "NoSuchKey")
                throw FileNotFoundException("업로드할 경로가 없습니다")
        }
        return amazonS3Client.getUrl(bucket, PathUtils.getPath(path)).toString()
    }

    /**
     * S3에 파일 최종 업로드 처리( 멀티파트 처리 )
     * @param s3UploadFile 업로드할 파일
     * @param path S3에 저장될 상세 경로(경로 + 파일명)
     * @return
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class, IOException::class)
    fun putS3(s3UploadFile: MultipartFile, path: Path): String {
        try {
            val metadata = ObjectMetadata()
            metadata.contentLength = IOUtils.toByteArray(s3UploadFile.inputStream).size.toLong()
            metadata.contentType = s3UploadFile.contentType

            amazonS3Client.putObject(
                PutObjectRequest(bucket, PathUtils.getPath(path), s3UploadFile.inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("S3 쓰기 권한이 없습니다 S3계정의 권한을 확인하세요")
            if (e.statusCode == HttpStatus.NOT_FOUND.value() && e.errorCode == "NoSuchKey")
                throw FileNotFoundException("업로드할 경로가 없습니다")
        }
        return amazonS3Client.getUrl(bucket, PathUtils.getPath(path)).toString()
    }

    /**
     * S3 파일 오브젝트 이동 처리
     * @param path
     * @param movePath
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class)
    fun move(path: Path, movePath: Path) {
        try {
            amazonS3Client.copyObject(CopyObjectRequest(bucket, path.toString(), bucket, movePath.toString()))
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("파일에 대한 권한 없습니다 S3계정의 권한을 확인하세요")
            if (e.statusCode == HttpStatus.NOT_FOUND.value() && e.errorCode == "NoSuchKey")
                throw FileNotFoundException("이동할 파일이 없습니다")
        }
    }

    /**
     * 파일다운로드
     * 파일 다운로드 예외처리시 유의 사항
     * FileDownloadFailException 임의 예외 발생시 Transactional 에 의해 롤백되지 않도록 noRollbackFor 처리 필요
     * @param path
     * @return
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class)
    fun download(path: Path, bucket: String?): Resource? {
        return try {
            val s3Object = amazonS3Client.getObject(GetObjectRequest(bucket, PathUtils.getPath(path)))
            val resource = InputStreamResource(s3Object.objectContent)
            log.info("================== Downloade File Complete ! ==================")
            resource
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("파일에 대한 권한 없습니다 S3계정의 권한을 확인하세요")
            if (e.statusCode == HttpStatus.NOT_FOUND.value() && e.errorCode == "NoSuchKey")
                throw FileNotFoundException("다운로드할 파일이 없습니다")
            null
        }
    }

    /**
     * 바이트 형식으로 다운로드 받기
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    @Throws(IOException::class, AmazonServiceException::class, AmazonClientException::class)
    fun byteDownload(path: Path, fileName: String): ResponseEntity<ByteArray> {
        try { // String s3fullpath= path+"/"+fileName;
            var fileName = fileName
            val getObjectRequest = GetObjectRequest(bucket, path.toString())
            val s3Object = amazonS3Client.getObject(getObjectRequest)
            val objectInputStream = s3Object.objectContent
            val bytes = IOUtils.toByteArray(objectInputStream)
            fileName = URLEncoder.encode(fileName, "UTF-8").replace("\\+".toRegex(), "%20")
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
            httpHeaders.contentLength = bytes.size.toLong()
            httpHeaders.setContentDispositionFormData("attachment", fileName)
            return ResponseEntity(bytes, httpHeaders, HttpStatus.OK)
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("파일에 대한 권한 없습니다 S3계정의 권한을 확인하세요")
            if (e.statusCode == HttpStatus.NOT_FOUND.value() && e.errorCode == "NoSuchKey")
                throw FileNotFoundException("다운로드할 파일이 없습니다")
            return ResponseEntity.noContent().build()
        }
    }

    /**
     * 파일 삭제
     * 파일이 존재 하지 않는 경우에는 에러 메세지만 출력 그외 에러 발생시에는 에러발생시킴
     * @param path
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class)
    fun delete(path: Path) {
        try {
            val isExistObject = amazonS3Client.doesObjectExist(bucket, PathUtils.getPath(path))
            if (!isExistObject)
                throw FileNotFoundException("삭제할 파일이 없습니다")
            amazonS3Client.deleteObject(bucket, PathUtils.getPath(path))

            log.info("[S3] File Deleted : {}", PathUtils.getPath(path))
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("파일에 대한 권한 없습니다 S3계정의 권한을 확인하세요")
        }
    }

    /**
     * 다중 파일 삭제
     * @param fileList
     */
    fun delete(path: Path, fileList: List<Path>) {
        fileList.forEach { delete(Paths.get(path.toString(), it.toString())) }
    }

    /**
     * 다중 파일 삭제
     * @param fileList
     */
    fun delete(fileList: List<Path>) {
        fileList.forEach { delete(it) }
    }

    /**
     * S3에서 조회된 리스트 다중 파일 삭제
     * @param result
     */
    fun delete(result: ListObjectsV2Result) {
        result.objectSummaries.forEach { delete(Paths.get(it.key)) }
    }

    /**
     * 특정 버켓에 들어있는 데이터 정보 가져오기
     * prefix 를 지정하지 않으면 모든 파일이 출력됨 최대 1000개 제한
     * @return
     */
    @Throws(AmazonServiceException::class, AmazonClientException::class)
    fun getObjectsListFromFolder(delimiter: String, prefix: Path): ListObjectsV2Result {
        return try {
            amazonS3Client.listObjectsV2(bucket, PathUtils.getPath(prefix))
        } catch (e: AmazonServiceException) {
            log.error(e.message)
            if (e.statusCode == HttpStatus.FORBIDDEN.value() && e.errorCode == "AccessDenied")
                throw AccessDeniedException("S3 일기 권한이 없습니다 S3계정의 권한을 확인하세요")
            ListObjectsV2Result()
        }
    }

    /**
     * 특정 버켓에 들어있는 파일 리스트 가져오기
     * prefix 를 지정하지 않으면 모든 파일이 출력됨 최대 1000개 제한
     * @return
     */
    fun getFileList(delimiter: String, prefix: Path): List<S3FileInfo> {
        val objects = getObjectsListFromFolder(delimiter, prefix).objectSummaries

        return objects.map {
            S3FileInfo(
                it.bucketName,
                it.key,
                it.size,
                it.lastModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                it.storageClass
            )
        }.toList()
    }
}