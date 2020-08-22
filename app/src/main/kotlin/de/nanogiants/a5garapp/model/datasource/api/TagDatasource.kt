package de.nanogiants.a5garapp.model.datasource.api

import de.nanogiants.a5garapp.model.entities.web.TagWebEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface TagDatasource {

  @GET("tags")
  suspend fun getAllTags(): List<TagWebEntity>

  @GET("tags/{id}")
  suspend fun getTagByID(@Path("id") id: Int): TagWebEntity
}