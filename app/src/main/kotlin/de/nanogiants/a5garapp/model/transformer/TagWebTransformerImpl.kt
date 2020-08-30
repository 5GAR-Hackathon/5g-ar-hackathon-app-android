package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.TagLocalEntity
import de.nanogiants.a5garapp.model.entities.web.TagWebEntity
import javax.inject.Inject

class TagWebTransformerImpl @Inject constructor() : TagWebTransformer {

  override fun toModel(entity: TagWebEntity): Tag {
    return with(entity) {
      Tag(id = id, name = name)
    }
  }

  override fun toModel(entity: TagLocalEntity): Tag {
    return with(entity) {
      Tag(id = id, name = name)
    }
  }
}