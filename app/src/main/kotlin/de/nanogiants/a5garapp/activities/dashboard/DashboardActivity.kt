package de.nanogiants.a5garapp.activities.dashboard

import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.huawei.hms.maps.CameraUpdate
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.MapFragment
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.common.util.DistanceCalculator
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MapStyleOptions
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardPOIAdapter
import de.nanogiants.a5garapp.activities.favorites.FavoritesActivity
import de.nanogiants.a5garapp.activities.filter.FilterActivity
import de.nanogiants.a5garapp.activities.listeners.OnSnapPositionChangeListener
import de.nanogiants.a5garapp.activities.listeners.SnapOnScrollListener
import de.nanogiants.a5garapp.activities.poidetail.POIDetailActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.utils.JSONReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : BaseActivity(), OnMapReadyCallback, OnSnapPositionChangeListener {

  override val binding: ActivityDashboardBinding by viewBinding(ActivityDashboardBinding::inflate)

  lateinit var poiAdapter: DashboardPOIAdapter

  lateinit var poiLayoutManager: LinearLayoutManager

  lateinit var poiSnapHelper: LinearSnapHelper

  @Inject
  lateinit var poiDatastore: POIDatastore

  val loadFromWeb: Boolean = false

  private var huaweiMap: HuaweiMap? = null

  lateinit var mapFragment: MapFragment

  var markerDrawn: Boolean = false

  var markers: MutableList<Marker> = mutableListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
  }

  override fun initView() {
    binding.toolbar.title = "Places"

    poiLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    poiAdapter = DashboardPOIAdapter()
    poiAdapter.onPOIClicked = this::onPOIClicked

    binding.dashboardPoiRecyclerview.apply {
      layoutManager = poiLayoutManager
      adapter = poiAdapter
    }

    poiSnapHelper = LinearSnapHelper()
    poiSnapHelper.attachToRecyclerView(binding.dashboardPoiRecyclerview)

    val snapOnScrollListener =
      SnapOnScrollListener(poiSnapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this)
    binding.dashboardPoiRecyclerview.addOnScrollListener(snapOnScrollListener)

    binding.bottomNavigationView.selectedItemId = R.id.places
    binding.bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.favorites -> {
          val intent = Intent(this, FavoritesActivity::class.java)
          // intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

          startActivity(intent)
          overridePendingTransition(0, 0)
          true
        }
        else -> false
      }
    }

    val huaweiMapOptions = HuaweiMapOptions()
    huaweiMapOptions.compassEnabled(true)
    huaweiMapOptions.zoomGesturesEnabled(true)
    mapFragment = MapFragment.newInstance(huaweiMapOptions)

    val fragmentManager: FragmentManager = fragmentManager
    fragmentManager.beginTransaction().apply {
      add(R.id.mapFragmentContainer, mapFragment)
    }.commit()

    mapFragment.getMapAsync(this)
  }

  override fun onResume() {
    super.onResume()


  }

  private fun onPOIClicked(poi: POI, sharedElement: View) {
    Timber.d("Clicked $poi")

    val intent = Intent(this, POIDetailActivity::class.java)
    intent.putExtra("POI", poi)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this@DashboardActivity,
      sharedElement,
      "poiBackgroundImage"
    )

    startActivity(intent, options.toBundle())
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.dashboard, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.filter -> {
        val intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.nothing);
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onMapReady(map: HuaweiMap) {
    val styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
    huaweiMap = map.apply {
      isMyLocationEnabled = true
      //uiSettings.isMyLocationButtonEnabled = true
      // setMapStyle(styleOptions)
    }

    lifecycleScope.launch {
      try {
        markers.map { it.remove() }
        markers = mutableListOf()

        if (loadFromWeb) {
          withContext(Dispatchers.IO) { poiDatastore.getAllPOIs() }.let {
            Timber.d("Loaded all $it")
            poiAdapter.clear()
            poiAdapter.addAll(it)

            it.map { poi -> markers.add(addMarker(poi)) }
          }
        } else {
          withContext(Dispatchers.IO) { JSONReader.getPOIsFromAssets(this@DashboardActivity) }.let {
            Timber.d("Loaded all $it")
            poiAdapter.clear()
            poiAdapter.addAll(it)

            it.map { poi -> markers.add(addMarker(poi)) }
          }
        }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
  }

  private fun centerMapOnPOI(poi: POI) {
    val cameraPosition =
      CameraPosition(LatLng(poi.coordinates.lat, poi.coordinates.lng), 15f, 2.0f, 0.0f)
    val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)

    huaweiMap?.animateCamera(cameraUpdate);

    markers.sortBy {
      DistanceCalculator.computeDistanceBetween(
        it.position,
        LatLng(poi.coordinates.lat, poi.coordinates.lng)
      )
    }

    markers.map { it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)) }
    markers[0].setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_secondary))
  }

  override fun onSnapPositionChange(position: Int) {
    Timber.d("Got to the possition $position")
    val poi = poiAdapter.get(position)
    centerMapOnPOI(poi)
  }

  private fun addMarker(poi: POI): Marker {
    Timber.d("Drawing on map ${huaweiMap == null} $markerDrawn ${poi.id}")

    val options: MarkerOptions = MarkerOptions()
      .position(LatLng(poi.coordinates.lat, poi.coordinates.lng))
      .title(poi.name)
      .snippet(poi.description)
      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))

    return huaweiMap!!.addMarker(options)
  }
}