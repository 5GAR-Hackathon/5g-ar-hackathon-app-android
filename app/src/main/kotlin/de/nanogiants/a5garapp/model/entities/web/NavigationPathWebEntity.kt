/**
 * Created by appcom interactive GmbH on 06.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.web

import com.huawei.hms.maps.model.LatLng
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavigationPathWebEntity(
  val duration: Float,
  val durationText: String,
  val durationInTrafficText: String,
  val durationInTraffic: Float,
  val distance: Float,
  val startLocation: NavigationLatLngWebEntity,
  val startAddress: String,
  val endLocation: NavigationLatLngWebEntity,
  val endAddress: String,
  val distanceText: String,
  val steps: List<NavigationStepWebEntity>
)