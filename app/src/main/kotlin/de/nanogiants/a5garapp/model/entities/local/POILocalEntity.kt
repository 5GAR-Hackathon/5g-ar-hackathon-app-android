/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.local

import android.accounts.AuthenticatorDescription
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class POILocalEntity(
  val id: Int,
  val name: String,
  val tags: List<Int>,
  val description: String,
  val coordinates: CoordinatesLocalEntity,
  val images: List<String>
)