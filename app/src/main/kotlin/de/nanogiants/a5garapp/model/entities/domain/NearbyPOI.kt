package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NearbyPOI(
  override val id: Int,
  override val name: String,
  override val description: String,
  override val tags: List<Tag>,
  override val coordinates: Coordinates,
  override val images: List<Image>,
  override val reviews: List<Review>,
  override val upvotes: Int,
  override val downvotes: Int,
  override val openingHours: List<OpeningHour>,
  val address: String,
  val distance: Float,
  val url: String
) : POI(id, name, description, tags, coordinates, images, reviews, upvotes, downvotes, openingHours)