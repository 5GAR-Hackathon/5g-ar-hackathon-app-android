package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.TagLocalEntity
import de.nanogiants.a5garapp.model.entities.web.TagWebEntity

interface TagWebTransformer {

  fun toModel(entity: TagWebEntity): Tag

  fun toModel(entity: TagLocalEntity): Tag
}