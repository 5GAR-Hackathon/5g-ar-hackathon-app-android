/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.local

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpeningHourLocalEntity(
  val day: String,
  val hours: String
)