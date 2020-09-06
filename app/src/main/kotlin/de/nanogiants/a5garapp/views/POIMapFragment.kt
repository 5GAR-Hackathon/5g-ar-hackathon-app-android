/**
 * Created by appcom interactive GmbH on 05.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.views

import android.os.Bundle
import com.huawei.hms.maps.CameraUpdate
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.MapFragment
import com.huawei.hms.maps.common.util.DistanceCalculator
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI


class POIMapFragment : MapFragment() {

  private var huaweiMap: HuaweiMap? = null

  private var markers: MutableList<Marker> = mutableListOf()

  companion object {
    fun newInstance(options: HuaweiMapOptions): POIMapFragment {
      var var1: Bundle?
      Bundle().also { var1 = it }.putParcelable("HuaweiMapOptions", options)
      var var2: POIMapFragment?
      POIMapFragment().also { var2 = it }.arguments = var1
      return var2!!
    }
  }

  fun setMap(map: HuaweiMap) {
    huaweiMap = map.apply {
      isMyLocationEnabled = true
    }

    // huaweiMap!!.setInfoWindowAdapter(POIMapWindowAdapter(context))
  }

  fun clearPOIs() {
    markers.map { it.remove() }
    markers = mutableListOf()
  }

  fun setPOIs(pois: List<POI>, autoCenter: Boolean = true, offsetToSouthInMeters: Float = 0.0f) {
    pois.map { poi -> markers.add(addMarker(poi)) }

    if (autoCenter && pois.size > 0) {
      centerMapOnPOI(pois[0], offsetToSouthInMeters)
    }
  }

  fun addPOI(poi: POI, autoCenter: Boolean = true, offsetToSouthInMeters: Float = 0.0f) {
    markers.add(addMarker(poi))

    if (autoCenter) {
      centerMapOnPOI(poi, offsetToSouthInMeters)
    }
  }

  fun enableInteractiveMap(enable: Boolean) {
    huaweiMap?.uiSettings?.setAllGesturesEnabled(enable)
    huaweiMap?.uiSettings?.isMyLocationButtonEnabled = false
  }

  fun centerMapOnPOI(poi: POI, offsetToSouthInMeters: Float = 0.0f) {
    val cameraPosition =
      CameraPosition(newCoordinatesWithOffsetToNorth(poi, offsetToSouthInMeters), 15f, 2.0f, 0.0f)
    val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)

    huaweiMap?.animateCamera(cameraUpdate);

    markers.sortBy {
      DistanceCalculator.computeDistanceBetween(
        it.position,
        LatLng(poi.coordinates.lat, poi.coordinates.lng)
      )
    }



    markers.map { it.alpha = 0.75f }
    markers[0].alpha = 1f
  }

  private fun newCoordinatesWithOffsetToNorth(poi: POI, meters: Float): LatLng {
    val earthRadius = 6378.0
    val pi = Math.PI;
    val lat = poi.coordinates.lat - ((meters / 1000) / earthRadius) * (180 / pi)

    return LatLng(lat, poi.coordinates.lng)
  }

  private fun addMarker(poi: POI): Marker {
    val id = context.resources.getIdentifier(
      getDrawableNameForTag(poi.tags[0].id, false),
      "drawable",
      context.packageName
    )

    val options: MarkerOptions = MarkerOptions()
      .position(LatLng(poi.coordinates.lat, poi.coordinates.lng))
      .title(poi.name)
      .icon(BitmapDescriptorFactory.fromResource(id)) // R.drawable.ic_pin

    return huaweiMap!!.addMarker(options)
  }

  private fun getDrawableNameForTag(tagId: Int, selected: Boolean): String {
    val name = when (tagId) {
      1 -> "ic_coffee" // restaurant

      2 -> "ic_map_outline" // place

      3 -> "ic_image_outline" // museum

      4 -> "ic_color_palette_outline" // art

      5 -> "ic_speaker" // concert

      6 -> "ic_flag" // kiosk

      7 -> "ic_flag" // bar

      8 -> "ic_flag" // zoo

      9 -> "ic_music_outline" // music

      10 -> "ic_anchor" // water

      11 -> "ic_film_outline" // theater

      12 -> "ic_resource_package" // startup

      13 -> "ic_briefcase_outline" // business

      14 -> "ic_flag" // lookout

      15 -> "ic_home_outline__1_" // architecture

      16 -> "ic_flag" // hotel

      17 -> "ic_flag" // historic
      else -> ""
    }

    return "$name${if (selected) "_selected" else ""}"
  }
}