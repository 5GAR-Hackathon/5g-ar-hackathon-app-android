package de.nanogiants.a5garapp.model.datasource.api

import de.nanogiants.a5garapp.model.entities.web.NavigationBodyWebEntity
import de.nanogiants.a5garapp.model.entities.web.POIWebEntity
import de.nanogiants.a5garapp.model.entities.web.WalkingNavigationWebEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NavigationDatasource {

  @POST("walking") // bicycling, driving
  suspend fun getWalkingNavigation(
    @Query("key") key: String,
    @Body body: NavigationBodyWebEntity
  ): WalkingNavigationWebEntity
}