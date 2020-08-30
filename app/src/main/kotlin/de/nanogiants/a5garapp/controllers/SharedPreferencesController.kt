package de.nanogiants.a5garapp.controllers

import android.content.SharedPreferences
import android.location.Location
import de.nanogiants.a5garapp.model.entities.domain.POI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
interface SharedPreferencesController {

  suspend fun getBookmarkedPOIs(): List<POI>

  suspend fun bookmarkPOI(poi: POI)

  suspend fun isPOIBookmarked(poi: POI): Boolean
}

