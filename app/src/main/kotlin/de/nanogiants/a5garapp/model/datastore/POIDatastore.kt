package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI

interface POIDatastore {

  suspend fun getPOIsbyLocation(coordinates: Coordinates, distance: Int = 2000): List<POI>

  suspend fun getAllPOIs(): List<POI>

  suspend fun getPOIByID(id: Int): POI
}