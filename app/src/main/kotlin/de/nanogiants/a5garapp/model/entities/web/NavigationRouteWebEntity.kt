/**
 * Created by appcom interactive GmbH on 06.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.web

import com.huawei.hms.maps.model.LatLngBounds
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavigationRouteWebEntity(
  val paths: List<NavigationPathWebEntity>,
  val bounds: NavigationBoundsWebEntity
)