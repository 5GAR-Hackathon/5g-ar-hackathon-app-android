package de.nanogiants.a5garapp.model.datastore

import com.huawei.hms.maps.model.LatLng
import de.nanogiants.a5garapp.model.datasource.api.NavigationDatasource
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.web.NavigationBodyWebEntity
import de.nanogiants.a5garapp.model.entities.web.NavigationLatLngWebEntity
import de.nanogiants.a5garapp.model.entities.web.WalkingNavigationWebEntity
import javax.inject.Inject

class NavigationDatastoreImpl @Inject constructor() : NavigationDatastore {

  @Inject
  lateinit var navigationDatasource: NavigationDatasource

  override suspend fun getWalkingNavigation(
    origin: Coordinates,
    destination: Coordinates
  ): WalkingNavigationWebEntity {
    val key =
      "CgB6e3x9gfqxB02ElADVb3qDLuH58sBFXY8KZPSgdV6zGn1c7LI/EHbu9fXDxtLTdwpSOHQGzaiv9mcrcJ1K9iaC"
    val body = NavigationBodyWebEntity(
      origin = NavigationLatLngWebEntity(origin.lat, origin.lng),
      destination = NavigationLatLngWebEntity(destination.lat, destination.lng)
    )

    return navigationDatasource.getWalkingNavigation(key, body)
  }
}