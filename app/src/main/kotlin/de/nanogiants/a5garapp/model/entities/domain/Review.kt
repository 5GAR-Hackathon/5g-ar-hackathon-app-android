/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Review(
  val poiId: Int,
  val userName: String,
  val content: String,
  val createdAt: String,
  val rating: Float,
  val likeCount: Int
) : Serializable
