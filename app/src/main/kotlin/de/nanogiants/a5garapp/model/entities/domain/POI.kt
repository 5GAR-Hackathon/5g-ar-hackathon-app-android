package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
open class POI(
  open val id: Int,
  open val name: String,
  open val description: String,
  open val tags: List<Tag>,
  open val coordinates: Coordinates,
  open val images: List<Image>,
  open val reviews: List<Review>,
  open val upvotes: Int,
  open val downvotes: Int,
  open val openingHours: List<OpeningHour>,
  open val arModelName: String?
) : Serializable