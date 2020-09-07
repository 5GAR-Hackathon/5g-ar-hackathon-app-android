package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class NearbyPOI(
  override val id: Int,
  override val name: String,
  override val description: String,
  override val tags: List<Tag>,
  override val coordinates: Coordinates,
  override val imageUrls: List<String>,
  override val reviews: List<Review>,
  override val rating: Float,
  override val openingHours: List<OpeningHour>,
  val address: String,
  val distance: Float,
  val url: String
) : POI(id, name, description, tags, coordinates, imageUrls, reviews, rating, openingHours)