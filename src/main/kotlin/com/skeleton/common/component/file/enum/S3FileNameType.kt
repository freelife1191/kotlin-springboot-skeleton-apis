package com.skeleton.common.component.file.enum

/**
 * 파일명 타입
 * Created by KMS on 2021/04/16.
 */
enum class S3FileNameType {
    UUID,     // 파일명을 시간기반 UUID로 변환해서 저장함(기본값)
    ORIGINAL, // Original 파일명을 저장함
    CUSTOM    // 그외 커스텀한 파일명으로 저장함
}