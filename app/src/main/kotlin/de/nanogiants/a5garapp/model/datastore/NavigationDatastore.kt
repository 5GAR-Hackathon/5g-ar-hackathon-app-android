package de.nanogiants.a5garapp.model.datastore

import com.huawei.hms.maps.model.LatLng
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.web.WalkingNavigationWebEntity

interface NavigationDatastore {

  suspend fun getWalkingNavigation(origin: Coordinates, destination: Coordinates): WalkingNavigationWebEntity
}