/**
 * Created by appcom interactive GmbH on 30.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.model.datastore

import de.nanogiants.a5garapp.model.entities.domain.Review


interface ReviewDatastore {

  suspend fun getReviewsForPOI(id: Int): List<Review>

}