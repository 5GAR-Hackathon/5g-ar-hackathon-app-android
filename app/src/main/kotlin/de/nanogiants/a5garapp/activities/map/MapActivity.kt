package de.nanogiants.a5garapp.activities.map

import android.os.Bundle
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityMapBinding

class MapActivity : BaseActivity(), OnMapReadyCallback {

  override val binding: ActivityMapBinding by viewBinding(ActivityMapBinding::inflate)
  private var huaweiMap: HuaweiMap? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_map)
    MapsInitializer.setApiKey("CgB6e3x9gfqxB02ElADVb3qDLuH58sBFXY8KZPSgdV6zGn1c7LI/EHbu9fXDxtLTdwpSOHQGzaiv9mcrcJ1K9iaC")
    binding.mapView.onCreate(savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY))
    binding.mapView.getMapAsync(this)
  }

  override fun initView() {
    // currently empty
  }

  override fun onMapReady(map: HuaweiMap) {
    huaweiMap = map
    huaweiMap?.isMyLocationEnabled = true
    huaweiMap?.uiSettings?.isMyLocationButtonEnabled = true
  }

  override fun onStart() {
    super.onStart()
    binding.mapView.onStart()
  }

  override fun onStop() {
    super.onStop()
    binding.mapView.onStop()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.mapView.onDestroy()
  }

  override fun onPause() {
    binding.mapView.onPause()
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapView.onLowMemory()
  }

  companion object {
    private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
  }
}