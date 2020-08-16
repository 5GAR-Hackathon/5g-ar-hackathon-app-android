package de.nanogiants.a5garapp.controllers

import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Looper
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.LocationAvailability
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.LocationSettingsRequest
import com.huawei.hms.location.LocationSettingsStatusCodes
import de.nanogiants.a5garapp.BaseActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class LocationControllerImpl @Inject constructor() : LocationController {

  @Inject
  lateinit var activity: BaseActivity

  private val locationState = MutableStateFlow<Location?>(null)

  private val fusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(activity) }
  private val settingsClient by lazy { LocationServices.getSettingsClient(activity) }
  private val locationRequest by lazy {
    LocationRequest().apply {
      interval = 10000
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
  }
  private val locationCallback by lazy {
    object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null) {
          val locations = locationResult.locations
          locations.lastOrNull()?.let {
            locationState.value = it
            Timber.d("onLocationResult location[Longitude,Latitude,Accuracy]: ${it.longitude}, ${it.latitude}, ${it.accuracy}")
          }
        }
      }

      override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
        if (locationAvailability != null) {
          val flag = locationAvailability.isLocationAvailable
          Timber.d("onLocationAvailability isLocationAvailable:$flag")
        }
      }
    }
  }

  override fun getLocation(): StateFlow<Location?> {
    settingsClient.checkLocationSettings(LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build())
      .addOnSuccessListener {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
      }
      .addOnFailureListener {
        when ((it as ApiException).statusCode) {
          LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
            (it as ResolvableApiException).startResolutionForResult(activity, 0)
          } catch (sie: SendIntentException) {
            Timber.e("PendingIntent unable to execute request.")
          }
        }
      }
    return locationState
  }

  override suspend fun stopLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
      .addOnSuccessListener {
        Timber.d("removeLocationUpdatesWithCallback onSuccess")
      }
      .addOnFailureListener {
        Timber.d("removeLocationUpdatesWithCallback onFailure ${it.message}")
      }
  }
}