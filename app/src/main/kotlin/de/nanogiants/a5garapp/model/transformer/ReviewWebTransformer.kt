package de.nanogiants.a5garapp.model.transformer

import de.nanogiants.a5garapp.model.entities.domain.Review
import de.nanogiants.a5garapp.model.entities.local.ReviewLocalEntity

interface ReviewWebTransformer {

  fun toModel(entity: ReviewLocalEntity): Review
}