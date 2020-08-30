/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.utils

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.entities.domain.Tag
import de.nanogiants.a5garapp.model.entities.local.POILocalEntity
import de.nanogiants.a5garapp.model.entities.local.ReviewLocalEntity
import de.nanogiants.a5garapp.model.entities.local.TagLocalEntity
import de.nanogiants.a5garapp.model.transformer.POIWebTransformerImpl
import de.nanogiants.a5garapp.model.transformer.ReviewWebTransformerImpl
import de.nanogiants.a5garapp.model.transformer.TagWebTransformerImpl
import timber.log.Timber

class JSONReader {
  companion object {
    fun getPOIsFromAssets(context: Context): List<POI> {

      val listType = Types.newParameterizedType(List::class.java, POILocalEntity::class.java)
      val adapter: JsonAdapter<List<POILocalEntity>> = Moshi.Builder().build().adapter(listType)

      val file = "pois.json"

      val myjson = context.assets.open(file).bufferedReader().use { it.readText() }
      Timber.d("json $myjson")

      val poiTransformer = POIWebTransformerImpl()
      val tags = getTagsFromAssets(context)
      val reviews = getReviewsFromAssets(context)

      return (adapter.fromJson(myjson) ?: listOf()).map {
        poiTransformer.toModel(it, tags, reviews.filter { review -> review.poiId == it.id })
      }
    }

    fun getTagsFromAssets(context: Context): List<Tag> {

      val listType = Types.newParameterizedType(List::class.java, TagLocalEntity::class.java)
      val adapter: JsonAdapter<List<TagLocalEntity>> = Moshi.Builder().build().adapter(listType)

      val file = "tags.json"

      val myjson = context.assets.open(file).bufferedReader().use { it.readText() }
      val tagTransformer = TagWebTransformerImpl()

      return (adapter.fromJson(myjson) ?: listOf()).map { tagTransformer.toModel(it) }
    }

    fun getReviewsFromAssets(context: Context): List<Review> {

      val listType = Types.newParameterizedType(List::class.java, ReviewLocalEntity::class.java)
      val adapter: JsonAdapter<List<ReviewLocalEntity>> = Moshi.Builder().build().adapter(listType)

      val file = "reviews.json"

      val myjson = context.assets.open(file).bufferedReader().use { it.readText() }
      val reviewTransformer = ReviewWebTransformerImpl()

      return (adapter.fromJson(myjson) ?: listOf()).map { reviewTransformer.toModel(it) }
    }

    fun poiListToJSON(pois: List<POI>): String {

      val listType = Types.newParameterizedType(List::class.java, POI::class.java)
      val adapter: JsonAdapter<List<POI>> = Moshi.Builder().build().adapter(listType)

      return adapter.toJson(pois)
    }

    fun jsonToPOIList(json: String): List<POI> {

      val listType = Types.newParameterizedType(List::class.java, POI::class.java)
      val adapter: JsonAdapter<List<POI>> = Moshi.Builder().build().adapter(listType)

      return adapter.fromJson(json) ?: listOf()
    }
  }
}