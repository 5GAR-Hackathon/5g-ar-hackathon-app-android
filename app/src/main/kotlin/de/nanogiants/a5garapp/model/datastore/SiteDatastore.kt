/**
 * Created by appcom interactive GmbH on 06.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI


interface SiteDatastore {

  suspend fun getCafeSites(location: Coordinates, radiusInMeters: Int, limit: Int = 5): List<POI>

  suspend fun getBankSites(location: Coordinates, radiusInMeters: Int, limit: Int = 3): List<POI>
}