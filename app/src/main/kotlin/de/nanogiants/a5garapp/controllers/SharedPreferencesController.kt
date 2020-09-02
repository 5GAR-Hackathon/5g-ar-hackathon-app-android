package de.nanogiants.a5garapp.controllers

import de.nanogiants.a5garapp.model.entities.domain.POI
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface SharedPreferencesController {

  suspend fun getBookmarkedPOIs(): List<POI>

  suspend fun bookmarkPOI(poi: POI)

  suspend fun isPOIBookmarked(poi: POI): Boolean
}

