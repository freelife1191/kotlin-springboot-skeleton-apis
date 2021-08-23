package com.skeleton.common.domain.entity

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

/**
 * Created by LYT to 2021/04/02
 */
@Schema(hidden = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @field:Schema(title = "등록일시")
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @field:Schema(title = "수정일시")
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)