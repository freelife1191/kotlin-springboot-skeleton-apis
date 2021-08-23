package com.skeleton.common.component.file.controller

import com.skeleton.common.component.file.domain.S3FileInfo
import com.skeleton.common.component.file.domain.S3FileOption
import com.skeleton.common.component.file.domain.UploadFileResponse
import com.skeleton.common.component.file.enum.S3FileNameType
import com.skeleton.common.component.file.service.FileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by KMS on 2021/04/16.
 */
@RestController
@RequestMapping("/tutorial/file")
@Tag(name = "파일 업/다운로드 튜토리얼", description = "FileTutorialController")
class FileTutorialController(
    val fileService: FileService
) {
    val log: Logger = LogManager.getLogger()

    val savePath = "contents"

    @Operation(summary = "단일 파일 업로드", description = "한개의 파일 업로드시 사용")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Throws(Exception::class)
    fun upload(@RequestPart(value = "file") file: MultipartFile,
               @Parameter(name = "커스텀 파일명 지정(지정하지 않으면 기본값)")
               @RequestParam(required = false) customFileName: String?,
               @Parameter(
                   description = "파일명 생성 타입 지정(기본값: ORIGINAL) :: ORIGINAL - 오리지날 파일명, UUID - 시간기반 UUID로 저장파일명을 변환, CUSTOM - 커스텀 파일명으로 변환")
               @RequestParam(required = false) fileNameType: S3FileNameType?): ResponseEntity<UploadFileResponse> {
        return ResponseEntity.ok(fileService.upload(file, Paths.get(savePath), S3FileOption(customFileName, fileNameType)))
    }

    @Operation(summary = "다중 파일 업로드", description = "다수의 파일을 업로드할때 사용 다중 파일 업로드")
    @PostMapping("/mutiple", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart(value = "files") files: Array<MultipartFile>,
               @Parameter(name = "커스텀 파일명 지정(지정하지 않으면 기본값)")
               @RequestParam(required = false) customFileName: String?,
               @Parameter(
                   description = "파일명 생성 타입 지정(기본값: UUID) :: ORIGINAL - 오리지날 파일명, UUID - 시간기반 UUID로 저장파일명을 변환, CUSTOM - 커스텀 파일명으로 변환")
               @RequestParam(required = false) fileNameType: S3FileNameType?): ResponseEntity<List<UploadFileResponse>> {

        if(files.isEmpty())
            return ResponseEntity.noContent().build()

        return ResponseEntity.ok(fileService.upload(files, Paths.get(savePath), S3FileOption(customFileName, fileNameType)))
    }

    @Operation(summary = "S3 파일 리스트 조회", description = "S3에 저장된 특정 경로의 파일 리스트를 조회")
    @GetMapping
    fun getFileList(
            @Parameter(description = "S3에 저장된 특정 경로", example = "contents")
            @RequestParam("path") path: String):
            ResponseEntity<List<S3FileInfo>> {
        return ResponseEntity.ok(fileService.getFileList(Paths.get(path)))
    }

    @Operation(summary = "S3 파일 다운로드(Resource)", description = "S3에서 파일을 다운로드 받음(Resource 형태로 반환)")
    @GetMapping("/download")
    fun download(
        @Parameter(description = "S3에 저장된 경로", example = "contents/lion.jpeg")
        @RequestParam("path") path: String,
        @Parameter(description = "S3에 저장된 파일명", example = "lion.jpeg")
        @RequestParam("fileName") fileName: String,
        @Parameter(description = "S3에 저장된 파일 타입", example = "image/jpeg")
        @RequestParam("mediaType", required = false) mediaType : String?
        ): ResponseEntity<Resource>
    {
        var contentType: MediaType = MediaType.APPLICATION_OCTET_STREAM

        if (StringUtils.isNotEmpty(mediaType))
            contentType = MediaType.parseMediaType(mediaType.toString())

        return ResponseEntity.ok()
            // .contentType(MediaType.valueOf(mediaType))
            .contentType(contentType)
            .cacheControl(CacheControl.noCache())
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=${URLEncoder.encode(fileName, StandardCharsets.UTF_8)}"
            )
            .body(fileService.download(Paths.get(path)))
    }

    /**
     * Byte 파일 다운로드
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    @Operation(summary = "S3 파일 다운로드(Byte)", description = "S3에서 파일을 다운로드 받음(Byte 형태로 반환)")
    @GetMapping("/download-byte")
    @Throws(
        IOException::class
    )
    fun bytedownload(
        @Parameter(description = "S3에 저장된 경로", example = "contents/lion.jpeg")
        @RequestParam("path", required = true) path: String,
        @Parameter(description = "S3에 저장된 파일명", example = "lion.jpeg")
        @RequestParam("fileName", required = true) fileName: String
    ): ResponseEntity<ByteArray> {
        return fileService.download(Path.of(path), fileName)
    }

    /**
     * 단일 파일 삭제
     * @param fileName
     * @return
     */
    @Operation(summary = "단일 파일 삭제", description = "S3에서 특정 파일을 삭제")
    @DeleteMapping
    fun delete(@Parameter(description = "S3에 저장경로", example = "contents/lion.jpeg")
               @RequestParam("path") path: String): ResponseEntity<Any> =
        fileService.delete(Paths.get(path))

    /**
     * 다중 파일 삭제
     * @param fileList
     * @return
     */
    @Operation(summary = "다중 파일 삭제", description = "S3에서 특정 파일들을 삭제")
    @DeleteMapping("/list")
    fun delete(@Parameter(description = "파일명 포함 경로")
               @RequestParam("fileList") fileList: List<String>): ResponseEntity<Any> =
        fileService.deleteString(fileList)

    /**
     * 파일 경로 이동
     * @param fileName
     * @return
     */
    @Operation(summary = "파일 경로 이동", description = "S3에서 파일 이동")
    @PatchMapping
    fun move(@Parameter(description = "S3에 기존 저장경로", example = "contents/lion.jpeg")
             @RequestParam("path") path: String,
             @Parameter(description = "이동경로", example = "contents/move/lion.jpeg")
             @RequestParam("movePath") movePath: String): ResponseEntity<Any> =
        fileService.move(Paths.get(path), Paths.get(movePath))
}