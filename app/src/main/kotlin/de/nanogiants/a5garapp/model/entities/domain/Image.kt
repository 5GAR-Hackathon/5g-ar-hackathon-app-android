/**
 * Created by appcom interactive GmbH on 09.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Image(
  val url: String,
  val type: ImageType
) : Serializable