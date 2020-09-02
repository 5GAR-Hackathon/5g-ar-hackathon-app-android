package de.nanogiants.a5garapp.controllers

import android.content.SharedPreferences
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.utils.JSONReader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SharedPreferencesControllerImpl @Inject constructor() : SharedPreferencesController {

  @Inject
  lateinit var sharedPreferences: SharedPreferences

  override suspend fun getBookmarkedPOIs(): List<POI> {
    val json = sharedPreferences.getString("pois", "[]") ?: "[]"
    return JSONReader.jsonToPOIList(json)
  }

  override suspend fun bookmarkPOI(poi: POI) {
    var poiList = getBookmarkedPOIs()

    if (isPOIBookmarked(poi)) {
      poiList = poiList.filter { it -> poi.id != it.id }
    } else {
      val tempList = mutableListOf(poi)
      tempList.addAll(
        poiList
      )

      poiList = tempList.toList()
    }

    sharedPreferences.edit().putString("pois", JSONReader.poiListToJSON(poiList)).apply()
  }

  override suspend fun isPOIBookmarked(poi: POI): Boolean {
    val poiList = getBookmarkedPOIs()
    return poiList.any { it -> poi.id == it.id }
  }
}