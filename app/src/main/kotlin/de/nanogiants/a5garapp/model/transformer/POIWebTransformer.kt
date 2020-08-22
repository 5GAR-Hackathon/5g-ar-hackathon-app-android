package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity

interface POIWebTransformer {

  fun toModel(entity: POIWebEntity): POI
}