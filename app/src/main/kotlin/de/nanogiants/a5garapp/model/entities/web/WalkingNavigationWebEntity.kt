/**
 * Created by appcom interactive GmbH on 06.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.entities.web

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WalkingNavigationWebEntity(
  val returnCode: String,
  val returnDesc: String,
  val routes: List<NavigationRouteWebEntity>
)