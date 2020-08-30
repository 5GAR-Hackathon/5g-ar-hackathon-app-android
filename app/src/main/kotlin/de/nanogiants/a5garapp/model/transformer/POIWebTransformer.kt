package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.POILocalEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity

interface POIWebTransformer {

  fun toModel(entity: POIWebEntity, tags: List<Tag>): POI

  fun toModel(entity: POILocalEntity, tags: List<Tag>): POI
}