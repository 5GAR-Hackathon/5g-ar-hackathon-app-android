package de.nanogiants.a5garapp.activities.map

import android.app.FragmentManager
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.MapFragment
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.MapStyleOptions
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityMapBinding


class MapActivity : BaseActivity(), OnMapReadyCallback {

  override val binding: ActivityMapBinding by viewBinding(ActivityMapBinding::inflate)
  private var huaweiMap: HuaweiMap? = null

  lateinit var mapFragment: MapFragment

  override fun initView() {
    val huaweiMapOptions = HuaweiMapOptions()
    huaweiMapOptions.compassEnabled(true)
    huaweiMapOptions.zoomGesturesEnabled(true)
    mapFragment = MapFragment.newInstance(huaweiMapOptions)

    val fragmentManager: FragmentManager = fragmentManager
    fragmentManager.beginTransaction().apply {
      add(R.id.fragment_container, mapFragment)
    }.commit()

    mapFragment.getMapAsync(this)
  }

  override fun onMapReady(map: HuaweiMap) {
    val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
    huaweiMap = map.apply {
      isMyLocationEnabled = true
      uiSettings.isMyLocationButtonEnabled = true
      setMapStyle(styleOptions)
    }
  }
}