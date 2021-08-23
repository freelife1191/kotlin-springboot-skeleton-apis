package com.skeleton.common.domain.entity

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

/**
 * Created by LYT to 2021/04/02
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseLogEntity(
    @field:Schema(title = "등록일시")
    @CreatedDate
    var createdAt: LocalDateTime? = null
)