package com.skeleton.common.domain.entity

import io.swagger.v3.oas.annotations.media.Schema
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
class BaseIncludeDeleteEntity(
    @Schema(title = "삭제여부: 미삭제(0), 삭제(1)")
    var deleteFlag: Boolean? = null,

    @Schema(title = "삭제일시")
    var deletedAt: LocalDateTime? = null
): BaseEntity()