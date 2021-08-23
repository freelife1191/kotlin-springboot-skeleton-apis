package com.skeleton.common.repository

import com.skeleton.common.domain.entity.Codebook
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by LYT to 2021/04/12
 */
interface CodebookRepository: MongoRepository<Codebook, ObjectId> {
}