package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.entities.local.ReviewLocalEntity
import javax.inject.Inject

class ReviewWebTransformerImpl @Inject constructor() : ReviewWebTransformer {

  override fun toModel(entity: ReviewLocalEntity): Review {
    return with(entity) {
      Review(
        poiId = poiId,
        rating = rating,
        content = content,
        userName = username,
        createdAt = createdAt,
        likeCount = likeCount
      )
    }
  }
}