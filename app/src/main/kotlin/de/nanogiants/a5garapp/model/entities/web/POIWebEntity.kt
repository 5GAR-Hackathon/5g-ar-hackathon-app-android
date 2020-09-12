package de.nanogiants.a5garapp.model.entities.web

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class POIWebEntity(
  val id: Int,
  val name: String,
  val tags: List<Int>,
  val description: String?,
  val images: List<ImageWebEntity>,
  val coordinates: CoordinatesWebEntity
)