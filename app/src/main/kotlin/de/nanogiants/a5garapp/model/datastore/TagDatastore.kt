package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.entities.domain.Tag

interface TagDatastore {

  suspend fun getAllTags(): List<Tag>

  suspend fun getTagByID(id: Int): Tag
}