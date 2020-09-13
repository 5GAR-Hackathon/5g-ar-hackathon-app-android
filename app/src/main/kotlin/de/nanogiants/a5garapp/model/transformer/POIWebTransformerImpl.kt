package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.Image
import de.nanogiants.a5garapp.model.entities.domain.ImageType.NORMAL
import de.nanogiants.a5garapp.model.entities.domain.ImageType.PANORAMA
import de.nanogiants.a5garapp.model.entities.domain.OpeningHour
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.CoordinatesLocalEntity
import de.nanogiants.a5garapp.model.entities.local.OpeningHourLocalEntity
import de.nanogiants.a5garapp.model.entities.local.POILocalEntity
import de.nanogiants.a5garapp.model.entities.web.CoordinatesWebEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import javax.inject.Inject
import kotlin.random.Random

class POIWebTransformerImpl @Inject constructor() : POIWebTransformer {

  override fun toModel(entity: POIWebEntity, tags: List<Tag>, reviews: List<Review>): POI {
    return with(entity) {
      POI(
        id = id,
        name = name,
        tags = tags.filter { entity.tags.contains(it.id) },
        description = entity.description ?: "",
        coordinates = toModel(coordinates),
        images = entity.images.map {
          Image(
            "https://5gar.vercel.app/${it.url}",
            NORMAL
          )
        } + Image("panorama2", PANORAMA),
        reviews = reviews,
        upvotes = Random(id).nextInt(100, 300),
        downvotes = Random(id).nextInt(10, 80),
        openingHours = listOf(),
        arModelName = getARModelNameForPOI(id)
      )
    }
  }

  override fun toModel(entity: POILocalEntity, tags: List<Tag>, reviews: List<Review>): POI {
    return with(entity) {
      POI(
        id = id,
        name = name,
        tags = tags.filter { entity.tags.contains(it.id) },
        description = entity.description,
        coordinates = toModel(coordinates),
        images = entity.images.map { Image(it.url, NORMAL) } + Image("panorama2", PANORAMA),
        reviews = reviews,
        upvotes = Random(id).nextInt(100, 300),
        downvotes = Random(id).nextInt(10, 80),
        openingHours = (openingHours ?: listOf()).map { toModel(it) },
        arModelName = getARModelNameForPOI(id)
      )
    }
  }

  internal fun toModel(entity: CoordinatesWebEntity): Coordinates {
    return with(entity) {
      Coordinates(lat = lat, lng = lng)
    }
  }

  internal fun toModel(entity: CoordinatesLocalEntity): Coordinates {
    return with(entity) {
      Coordinates(lat = lat, lng = lng)
    }
  }

  internal fun toModel(entity: OpeningHourLocalEntity): OpeningHour {
    return with(entity) {
      OpeningHour(day = day, hours = hours)
    }
  }

  private fun getARModelNameForPOI(id: Int): String? {
    return when(id) {
      3 -> "tonhalle"
      15 -> "gehrybauten"
      16 -> "rheinturm"
      24, 25 -> "k21"
      29 -> "nrwforum"
      else -> null
    }
  }
}