package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class POI(
  val id: Int,
  val name: String,
  val description: String,
  val tags: List<Tag>,
  val coordinates: Coordinates,
  val imageUrls: List<String>,
  val reviews: List<Review>,
  val rating: Float
) : Serializable