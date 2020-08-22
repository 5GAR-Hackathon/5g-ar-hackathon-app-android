package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.web.CoordinatesWebEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import javax.inject.Inject

class POIWebTransformerImpl @Inject constructor() : POIWebTransformer {

  override fun toModel(entity: POIWebEntity): POI {
    return with(entity) {
      POI(id = id, name = name, tags = tags, coordinates = toModel(coordinates))
    }
  }

  internal fun toModel(entity: CoordinatesWebEntity): Coordinates {
    return with(entity) {
      Coordinates(lat = lat, lng = lng)
    }
  }
}