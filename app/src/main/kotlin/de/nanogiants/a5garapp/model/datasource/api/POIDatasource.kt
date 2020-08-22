package de.nanogiants.a5garapp.model.datasource.api

import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface POIDatasource {

  @GET("pois")
  suspend fun getPOIsbyLocation(
    @Query("location") location: String,
    @Query("distance") distance: Int
  ): List<POIWebEntity>

  @GET("pois")
  suspend fun getAllPOIs(): List<POIWebEntity>

  @GET("pois/{id}")
  suspend fun getPOIbyID(@Path("id") id: Int): POIWebEntity
}