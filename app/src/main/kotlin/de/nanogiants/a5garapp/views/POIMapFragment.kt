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

    huaweiMap!!.setInfoWindowAdapter(POIMapWindowAdapter(context))
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
    val options: MarkerOptions = MarkerOptions()
      .position(LatLng(poi.coordinates.lat, poi.coordinates.lng))
      .title(poi.name)
      .snippet(poi.description)
      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))

    return huaweiMap!!.addMarker(options)
  }
}