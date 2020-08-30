/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.local


data class ReviewLocalEntity(
  val poiId: Int,
  val username: String,
  val content: String,
  val createdAt: String,
  val rating: Float,
  val likeCount: Int
)
