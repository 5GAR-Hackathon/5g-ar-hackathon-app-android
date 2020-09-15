package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.datasource.api.POIDatasource
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.ImageType.NORMAL
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.transformer.POIWebTransformer
import javax.inject.Inject

class POIDatastoreImpl @Inject constructor() : POIDatastore {

  @Inject
  lateinit var poiDatasource: POIDatasource

  @Inject
  lateinit var tagDatastore: TagDatastore

  @Inject
  lateinit var poiWebTransformer: POIWebTransformer

  @Inject
  lateinit var reviewDatastore: ReviewDatastore

  override suspend fun getPOIsbyLocation(coordinates: Coordinates, distance: Int): List<POI> {
    val tags = this.fetchTags()

    return poiDatasource.getPOIsbyLocation("${coordinates.lat},${coordinates.lng}", distance)
      .map {
        val reviews: List<Review> = listOf() // reviewDatastore.getReviewsForPOI(it.id)
        poiWebTransformer.toModel(it, tags, reviews)
      }.filter { it.images.any { image -> image.type == NORMAL } }
  }

  override suspend fun getAllPOIs(): List<POI> {
    val tags = this.fetchTags()
    return poiDatasource.getAllPOIs().map {
      val reviews: List<Review> = listOf() // reviewDatastore.getReviewsForPOI(it.id)
      poiWebTransformer.toModel(it, tags, reviews)
    }.filter { it.images.any { image -> image.type == NORMAL } }
  }

  override suspend fun getPOIByID(id: Int): POI {
    val reviews: List<Review> = listOf() // reviewDatastore.getReviewsForPOI(it.id)
    return poiWebTransformer.toModel(poiDatasource.getPOIbyID(id), this.fetchTags(), reviews)
  }

  internal suspend fun fetchTags() = tagDatastore.getAllTags()
}