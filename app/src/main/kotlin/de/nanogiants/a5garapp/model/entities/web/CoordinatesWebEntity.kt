package de.nanogiants.a5garapp.model.entities.web

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoordinatesWebEntity(val lat: Double, val lng: Double)
