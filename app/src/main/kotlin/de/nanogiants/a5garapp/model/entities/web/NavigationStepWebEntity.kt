/**
 * Created by appcom interactive GmbH on 06.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.web

import com.huawei.hms.maps.model.LatLng
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavigationStepWebEntity(
  val duration: Float,
  val orientation: Int,
  val durationText: String,
  val distance: Float,
  val startLocation: NavigationLatLngWebEntity,
  val instruction: String,
  val action: String,
  val distanceText: String,
  val endLocation: NavigationLatLngWebEntity,
  val polyline: List<NavigationLatLngWebEntity>,
  val roadName: String
)
