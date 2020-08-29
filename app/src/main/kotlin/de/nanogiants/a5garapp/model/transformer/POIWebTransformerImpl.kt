package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.web.CoordinatesWebEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import java.util.Arrays
import javax.inject.Inject

class POIWebTransformerImpl @Inject constructor() : POIWebTransformer {

  override fun toModel(entity: POIWebEntity, tags: List<Tag>): POI {
    return with(entity) {
      POI(
        id = id,
        name = name,
        tags = tags.filter { it -> entity.tags.contains(it.id) },
        coordinates = toModel(coordinates),
        imageUrls = listOf(
          "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg",
          "https://www.deutsche-startups.de/app/uploads/2019/12/ds-duesseldorf-720x480.jpg"
        ) // TODO replace with actual images from web
      )
    }
  }

  internal fun toModel(entity: CoordinatesWebEntity): Coordinates {
    return with(entity) {
      Coordinates(lat = lat, lng = lng)
    }
  }
}