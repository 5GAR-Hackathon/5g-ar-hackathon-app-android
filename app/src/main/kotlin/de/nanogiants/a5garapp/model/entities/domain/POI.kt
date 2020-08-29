package de.nanogiants.a5garapp.model.entities.domain

data class POI(
  val id: Int,
  val name: String,
  val tags: List<Tag>,
  val coordinates: Coordinates
)