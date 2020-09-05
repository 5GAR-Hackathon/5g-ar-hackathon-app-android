/**
 * Created by appcom interactive GmbH on 05.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.Marker
import de.nanogiants.a5garapp.R
import timber.log.Timber


class POIMapWindowAdapter(context: Context) : HuaweiMap.InfoWindowAdapter {

  private val mWindow: View =
    LayoutInflater.from(context).inflate(R.layout.layout_poi_map_window, null, false)

  override fun getInfoWindow(marker: Marker): View? {
    val titleTextView: TextView = mWindow.findViewById(R.id.titleTextView)
    titleTextView.text = "Paris"

    Timber.d("fhdjsfdhshjkfdsjkh ${titleTextView.text}")

    return mWindow
  }

  override fun getInfoContents(p0: Marker?): View? {
    val titleTextView: TextView = mWindow.findViewById(R.id.titleTextView)
    titleTextView.text = "Paris"

    return mWindow
  }
}

