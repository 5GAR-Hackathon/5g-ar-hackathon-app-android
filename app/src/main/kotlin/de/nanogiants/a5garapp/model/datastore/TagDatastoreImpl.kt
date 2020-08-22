package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.datasource.api.TagDatasource
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.transformer.TagWebTransformer
import javax.inject.Inject

class TagDatastoreImpl @Inject constructor() : TagDatastore {

  @Inject
  lateinit var tagsDatasource: TagDatasource

  @Inject
  lateinit var tagWebTransformer: TagWebTransformer

  override suspend fun getAllTags(): List<Tag> {
    return tagsDatasource.getAllTags().map { tagWebTransformer.toModel(it) }
  }

  override suspend fun getTagByID(id: Int): Tag {
    return tagWebTransformer.toModel(tagsDatasource.getTagByID(id))
  }
}