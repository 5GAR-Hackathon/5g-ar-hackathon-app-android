package de.nanogiants.a5garapp.controllers

import android.location.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
interface LocationController {

  fun getLocation(): StateFlow<Location?>

  suspend fun stopLocationUpdates()
}