package de.nanogiants.a5garapp.model.entities.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class SelectableTag(val id: Int, val name: String, var selected: Boolean = false) :
  Serializable