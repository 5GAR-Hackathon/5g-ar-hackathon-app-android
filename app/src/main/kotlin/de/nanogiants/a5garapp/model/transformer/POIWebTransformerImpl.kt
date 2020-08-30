package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.CoordinatesLocalEntity
import de.nanogiants.a5garapp.model.entities.local.POILocalEntity
import de.nanogiants.a5garapp.model.entities.web.CoordinatesWebEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import javax.inject.Inject

class POIWebTransformerImpl @Inject constructor() : POIWebTransformer {

  override fun toModel(entity: POIWebEntity, tags: List<Tag>, reviews: List<Review>): POI {
    return with(entity) {
      POI(
        id = id,
        name = name,
        tags = tags.filter { it -> entity.tags.contains(it.id) },
        description = "",
        coordinates = toModel(coordinates),
        imageUrls = listOf(
          "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg"
        ),
        reviews = reviews,
        rating = reviews.map(Review::rating)
          .reduce { left: Float, right: Float -> left + right } / reviews.size
      )
    }
  }

  override fun toModel(entity: POILocalEntity, tags: List<Tag>, reviews: List<Review>): POI {
    return with(entity) {
      POI(
        id = id,
        name = name,
        tags = tags.filter { it -> entity.tags.contains(it.id) },
        description = entity.description,
        coordinates = toModel(coordinates),
        imageUrls = entity.images,
        reviews = reviews,
        rating = reviews.map(Review::rating)
          .reduce { left: Float, right: Float -> left + right } / reviews.size
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
}