package de.nanogiants.a5garapp.model.entities.domain

import java.io.Serializable

data class POI(
  val id: Int,
  val name: String,
  val tags: List<Tag>,
  val coordinates: Coordinates,
  val imageUrls: List<String>
) : Serializable