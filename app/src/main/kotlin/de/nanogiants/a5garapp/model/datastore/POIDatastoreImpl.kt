package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.datasource.api.POIDatasource
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.transformer.POIWebTransformer
import javax.inject.Inject

class POIDatastoreImpl @Inject constructor() : POIDatastore {

  @Inject
  lateinit var poiDatasource: POIDatasource

  @Inject
  lateinit var tagDatastore: TagDatastore

  @Inject
  lateinit var poiWebTransformer: POIWebTransformer

  override suspend fun getPOIsbyLocation(coordinates: Coordinates, distance: Int): List<POI> {
    val tags = this.fetchTags()
    return poiDatasource.getPOIsbyLocation("${coordinates.lat},${coordinates.lng}", distance)
      .map { poiWebTransformer.toModel(it, tags) }
  }

  override suspend fun getAllPOIs(): List<POI> {
    val tags = this.fetchTags()
    return poiDatasource.getAllPOIs().map { poiWebTransformer.toModel(it, tags) }
  }

  override suspend fun getPOIByID(id: Int): POI {
    return poiWebTransformer.toModel(poiDatasource.getPOIbyID(id), this.fetchTags())
  }

  internal suspend fun fetchTags() = tagDatastore.getAllTags()
}