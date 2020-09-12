package de.nanogiants.a5garapp.activities.dashboard

import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.panorama.Panorama
import com.huawei.hms.panorama.PanoramaInterface
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.R.id
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardPOIAdapter
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardTagAdapter
import de.nanogiants.a5garapp.activities.favorites.FavoritesActivity
import de.nanogiants.a5garapp.activities.listeners.OnSnapPositionChangeListener
import de.nanogiants.a5garapp.activities.listeners.SnapOnScrollListener
import de.nanogiants.a5garapp.activities.poidetail.POIDetailActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.model.datastore.NavigationDatastore
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.datastore.TagDatastore
import de.nanogiants.a5garapp.model.entities.domain.Coordinates
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.SelectableTag
import de.nanogiants.a5garapp.utils.JSONReader
import de.nanogiants.a5garapp.views.POIMapFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : BaseActivity(), OnMapReadyCallback, OnSnapPositionChangeListener,
  OnTouchListener {

  override val binding: ActivityDashboardBinding by viewBinding(ActivityDashboardBinding::inflate)

  lateinit var poiAdapter: DashboardPOIAdapter

  lateinit var poiLayoutManager: LinearLayoutManager

  lateinit var poiSnapHelper: LinearSnapHelper

  lateinit var tagAdapter: DashboardTagAdapter

  lateinit var tagLayoutManager: LinearLayoutManager

  @Inject
  lateinit var poiDatastore: POIDatastore

  @Inject
  lateinit var tagDataStore: TagDatastore

  @Inject
  lateinit var navigationDatastore: NavigationDatastore

  val loadFromWeb: Boolean = false

  lateinit var mapFragment: POIMapFragment

  lateinit var mLocalInstance: PanoramaInterface.PanoramaLocalInterface

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
  }

  override fun initView() {
    binding.toolbar.title = "Places"

    tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    tagAdapter = DashboardTagAdapter()
    tagAdapter.onTagClicked = this::onTagClicked

    binding.tagRecyclerView.collapse(true)
    binding.tagRecyclerView.apply {
      layoutManager = tagLayoutManager
      adapter = tagAdapter
    }

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
      SnapOnScrollListener(
        poiSnapHelper,
        SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
        this
      )
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

    mapFragment = POIMapFragment.newInstance(huaweiMapOptions)

    val fragmentManager: FragmentManager = fragmentManager
    fragmentManager.beginTransaction().apply {
      add(R.id.mapFragmentContainer, mapFragment)
    }.commit()

    mapFragment.getMapAsync(this)


    mLocalInstance = Panorama.getInstance().getLocalInstance(this)

    if (mLocalInstance.init() == 0) {
      val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.panorama2);
      if (mLocalInstance.setImage(uri, PanoramaInterface.IMAGE_TYPE_SPHERICAL) == 0) {
        val layout = binding.frameLayout
        val view = mLocalInstance.view
        layout.addView(view)

        view.setOnTouchListener(this)
      } else {
        Timber.e("erro2")
      }
    } else {
      Timber.e("erro1")
    }
  }

  override fun onResume() {
    super.onResume()

    lifecycleScope.launch {
      try {
        if (loadFromWeb) {
          withContext(Dispatchers.IO) { tagDataStore.getAllTags() }.let {
            tagAdapter.clear()
            tagAdapter.addAll(it.map { tag -> SelectableTag(tag.id, tag.name, false) })
          }
        } else {
          withContext(Dispatchers.IO) { JSONReader.getTagsFromAssets(this@DashboardActivity) }.let {
            Timber.d("Loaded all $it")
            tagAdapter.clear()
            tagAdapter.addAll(it.map { tag -> SelectableTag(tag.id, tag.name, false) })
          }
        }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
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
        /*val intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.nothing);*/

        binding.tagRecyclerView.toggle()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onMapReady(map: HuaweiMap) {
    mapFragment.setMap(map)
    mapFragment.navigationDatastore = navigationDatastore

    loadPOIsWithTags()
  }

  private fun loadPOIsWithTags(tagIDs: List<Int> = listOf()) {
    lifecycleScope.launch {
      try {
        if (loadFromWeb) {
          withContext(Dispatchers.IO) { poiDatastore.getAllPOIs() }.let {
            val filteredPOIs =
              it.filter { poi -> tagIDs.isEmpty() || poi.tags.any { tag -> tagIDs.contains(tag.id) } }

            poiAdapter.clear()
            poiAdapter.addAll(filteredPOIs)

            mapFragment.clearPOIs()
            mapFragment.addPOIs(filteredPOIs, true, 400.0f)
          }
        } else {
          withContext(Dispatchers.IO) { JSONReader.getPOIsFromAssets(this@DashboardActivity) }.let {
            val filteredPOIs =
              it.filter { poi -> tagIDs.isEmpty() || poi.tags.any { tag -> tagIDs.contains(tag.id) } }

            poiAdapter.clear()
            poiAdapter.addAll(filteredPOIs)

            mapFragment.clearPOIs()
            mapFragment.addPOIs(filteredPOIs, true, 400.0f)
          }
        }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
  }

  override fun onSnapPositionChange(position: Int) {
    val poi = poiAdapter.get(position)
    mapFragment.centerMapOnPOI(poi, 400.0f)

    binding.navigateButton.setOnClickListener {
      Timber.d("Alright. We are going")
      navigate(poi.coordinates, poiAdapter.get(position + 1).coordinates)
    }
  }

  private fun onTagClicked(tag: SelectableTag, position: Int) {
    tagAdapter.selectTag(position, !tag.selected)
    loadPOIsWithTags(tagAdapter.getAll().filter { it.selected }.map { it.id })
  }

  private fun navigate(from: Coordinates, to: Coordinates) {
    lifecycleScope.launch {
      try {
        withContext(Dispatchers.IO) { mapFragment.navigate(from, to) }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
  }

  override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
    mLocalInstance.updateTouchEvent(p1);
    return true;
  }
}

