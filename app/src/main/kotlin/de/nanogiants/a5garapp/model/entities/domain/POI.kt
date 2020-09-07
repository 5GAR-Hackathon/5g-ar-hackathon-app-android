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
  open val imageUrls: List<String>,
  open val reviews: List<Review>,
  open val rating: Float,
  open val openingHours: List<OpeningHour>
) : Serializable