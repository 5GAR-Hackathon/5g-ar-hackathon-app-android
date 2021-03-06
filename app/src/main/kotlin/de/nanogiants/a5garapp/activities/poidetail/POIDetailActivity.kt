package de.nanogiants.a5garapp.activities.poidetail

import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.ImageView.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.chip.Chip
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.mlsdk.tts.MLTtsConfig
import com.huawei.hms.mlsdk.tts.MLTtsConstants
import com.huawei.hms.mlsdk.tts.MLTtsEngine
import com.huawei.hms.panorama.Panorama
import com.huawei.hms.panorama.PanoramaInterface
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.arscene.ARSceneActivity
import de.nanogiants.a5garapp.activities.poidetail.adapters.POINearbyAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIOpeningHoursAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIPhotoAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIReviewAdapter
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.SharedPreferencesController
import de.nanogiants.a5garapp.databinding.ActivityPoiDetailBinding
import de.nanogiants.a5garapp.model.datastore.NavigationDatastore
import de.nanogiants.a5garapp.model.datastore.ReviewDatastore
import de.nanogiants.a5garapp.model.datastore.SiteDatastore
import de.nanogiants.a5garapp.model.entities.domain.Image
import de.nanogiants.a5garapp.model.entities.domain.ImageType.NORMAL
import de.nanogiants.a5garapp.model.entities.domain.ImageType.PANORAMA
import de.nanogiants.a5garapp.model.entities.domain.NearbyPOI
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.utils.Utilities
import de.nanogiants.a5garapp.views.POIMapFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class POIDetailActivity : BaseActivity(), OnMapReadyCallback {

  override val binding: ActivityPoiDetailBinding by viewBinding(ActivityPoiDetailBinding::inflate)

  lateinit var poiPhotoAdapter: POIPhotoAdapter

  lateinit var poiPhotoLayoutManager: GridLayoutManager

  lateinit var poiReviewAdapter: POIReviewAdapter

  lateinit var poiReviewLayoutManager: LinearLayoutManager

  lateinit var poiOpeningHoursAdapter: POIOpeningHoursAdapter

  lateinit var poiOpeningHoursLayoutManager: LinearLayoutManager

  lateinit var poiNearbyAdapter: POINearbyAdapter

  lateinit var poiNearbyLayoutManager: LinearLayoutManager

  @Inject
  lateinit var reviewDatastore: ReviewDatastore

  @Inject
  lateinit var siteDatastore: SiteDatastore

  @Inject
  lateinit var sharedPreferencesController: SharedPreferencesController

  @Inject
  lateinit var navigationDatastore: NavigationDatastore

  lateinit var poi: POI

  lateinit var mapFragment: POIMapFragment

  lateinit
  var optionsMenu: Menu

  lateinit var mlTtsEngine: MLTtsEngine

  private var isCurrentlyReading: Boolean = false

  lateinit var panorama: PanoramaInterface.PanoramaLocalInterface

  var panoramaWorks = true

  var isPanoramaShowing = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
  }

  override fun initView() {
    poi = intent.getSerializableExtra("POI") as POI
    Timber.d("New activity $poi")

    binding.toolbar.title = poi.name
    binding.descriptionTextView.text = poi.description

    binding.poiBackgroundImageView.load(
      if (poi.images.filter { it.type == NORMAL }.isEmpty()) {
        Utilities.IMAGE_DEFAULT
      } else {
        poi.images[0].url
      }
    )

    poiPhotoLayoutManager = GridLayoutManager(this, 4)

    poiPhotoAdapter = POIPhotoAdapter()
    poiPhotoAdapter.addAll(poi.images)
    poiPhotoAdapter.onPhotoClicked = this::onPhotoClicked

    binding.photoRecyclerView.apply {
      layoutManager = poiPhotoLayoutManager
      adapter = poiPhotoAdapter
    }

    poiReviewLayoutManager = LinearLayoutManager(this)
    poiReviewAdapter = POIReviewAdapter()
    poiReviewAdapter.addAll(poi.reviews)

    binding.reviewRecyclerView.apply {
      layoutManager = poiReviewLayoutManager
      adapter = poiReviewAdapter
    }

    binding.upvotesTextView.text = poi.upvotes.toString()
    binding.downvotesTextView.text = poi.downvotes.toString()

    poiOpeningHoursLayoutManager = LinearLayoutManager(this)
    poiOpeningHoursAdapter = POIOpeningHoursAdapter()
    poiOpeningHoursAdapter.addAll(poi.openingHours)

    binding.openingHoursConstraintLayout.visibility = View.GONE
    // if (poi.openingHours.isEmpty()) View.GONE else View.VISIBLE
    binding.openingHoursRecyclerView.apply {
      layoutManager = poiOpeningHoursLayoutManager
      adapter = poiOpeningHoursAdapter
    }

    poiNearbyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    poiNearbyAdapter = POINearbyAdapter()
    poiNearbyAdapter.onNearbyPOIClicked = this::onNearbyPOIClicked

    binding.nearbyRecyclerView.apply {
      layoutManager = poiNearbyLayoutManager
      adapter = poiNearbyAdapter
    }

    poi.tags.forEach {
      val chip = Chip(this@POIDetailActivity)
      chip.text = it.name.capitalize(Locale.getDefault())
      chip.setTextAppearanceResource(R.style.TagChipTextStyle)
      chip.setChipBackgroundColorResource(R.color.white)

      chip.isClickable = false
      chip.isCheckable = false

      binding.tagChipGroup.addView(chip)
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

    searchNearbyLocations()

    val mlConfigs: MLTtsConfig = MLTtsConfig()
      .setLanguage(MLTtsConstants.TTS_EN_US)
      .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
      .setSpeed(1.0f)
      .setVolume(1.0f)

    mlTtsEngine = MLTtsEngine(mlConfigs)

    binding.ttsButton.setOnClickListener {
      if (isCurrentlyReading) {
        mlTtsEngine.stop()
      } else {
        poi.description.chunked(450).map {
          mlTtsEngine.speak(it, MLTtsEngine.QUEUE_APPEND)
        }
      }

      isCurrentlyReading = !isCurrentlyReading

      val drawable = ContextCompat.getDrawable(
        this,
        if (isCurrentlyReading) R.drawable.ic_stop_circle else R.drawable.ic_play_circle
      )
      binding.ttsButton.setImageDrawable(drawable)
    }

    panorama = Panorama.getInstance().getLocalInstance(this)
    if (panorama.init() != 0) {
      Timber.w("Initializing panorama did not work")
      panoramaWorks = false
    }

    binding.arButton.visibility = if (poi.arModelName == null) GONE else VISIBLE
    binding.arButton.setOnClickListener { openModelInAR() }
  }

  override fun onDestroy() {
    super.onDestroy()
    mlTtsEngine.shutdown()
  }

  override fun onPause() {
    super.onPause()
    mlTtsEngine.stop()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.poi_detail, menu)

    optionsMenu = menu
    updateBookmarkOptionsIcon()

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.add_to_tour -> {
        return true
      }
      R.id.bookmark -> {
        lifecycleScope.launch {
          sharedPreferencesController.bookmarkPOI(poi)
          updateBookmarkOptionsIcon()
        }

        return true
      }
      android.R.id.home -> {
        if (isPanoramaShowing) {
          binding.panoramaLayout.visibility = GONE
          isPanoramaShowing = false
        } else {
          ActivityCompat.finishAfterTransition(this)

        }

        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun updateBookmarkOptionsIcon() {
    val menuItem: MenuItem = optionsMenu.findItem(R.id.bookmark)
    lifecycleScope.launch {
      menuItem.setIcon(if (sharedPreferencesController.isPOIBookmarked(poi)) R.drawable.ic_bookmark_white else R.drawable.ic_bookmark_white_outline)
    }
  }

  override fun onMapReady(map: HuaweiMap) {
    mapFragment.setMap(map)
    mapFragment.navigationDatastore = navigationDatastore
    mapFragment.enableInteractiveMap(false)

    mapFragment.clearPOIs()
    mapFragment.addPOI(poi)
  }

  private fun searchNearbyLocations() {
    lifecycleScope.launch {
      try {
        val cafePOIs = siteDatastore.getCafeSites(poi.coordinates, 3000)
        val bankPOIs = siteDatastore.getBankSites(poi.coordinates, 3000)

        val pois = (cafePOIs + bankPOIs).sortedBy { it.distance }

        mapFragment.addPOIs(
          pois = pois,
          autoCenter = false,
          customMarkers = true
        )

        Timber.d("Got ${pois.size} elements")

        binding.nearbyConstraintLayout.visibility = if (pois.isEmpty()) GONE else VISIBLE
        poiNearbyAdapter.addAll(pois)
      } catch (error: Exception) {
        Timber.e(error)
      }
    }
  }

  fun onNearbyPOIClicked(nearbyPOI: NearbyPOI) {
    lifecycleScope.launch {
      try {
        mapFragment.navigate(poi.coordinates, nearbyPOI.coordinates)
      } catch (error: Exception) {
        Timber.e(error)
      }
    }
  }

  private fun onPhotoClicked(image: Image, index: Int, imageView: ImageView) {
    if (image.type == NORMAL || !panoramaWorks) {
      StfalconImageViewer.Builder(
        this@POIDetailActivity,
        poi.images.filter { it.type == NORMAL }) { view, poiImage ->
        view.scaleType = ScaleType.FIT_CENTER
        view.load(poiImage.url)
      }
        .withTransitionFrom(imageView)
        .withHiddenStatusBar(false)
        .withStartPosition(index)
        .show()
    } else if (image.type == PANORAMA) {
      var id = Utilities.getResourceId(Utilities.PACKAGE_RAW, image.url, this)
      val uri = Uri.parse("android.resource://" + packageName + "/" + id);

      if (panorama.setImage(uri, PanoramaInterface.IMAGE_TYPE_SPHERICAL) == 0) {
        val layout = binding.panoramaLayout
        val view = panorama.view

        layout.removeAllViews()
        layout.addView(view)
        layout.visibility = VISIBLE

        isPanoramaShowing = true

        view.setOnTouchListener(object : OnTouchListener {
          override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            panorama.updateTouchEvent(p1)
            return true
          }
        })
      } else {
        Timber.e("Could not open this")
      }
    }
  }

  override fun onBackPressed() {
    if (isPanoramaShowing) {
      binding.panoramaLayout.visibility = GONE
      isPanoramaShowing = false
    } else {
      super.onBackPressed()
    }
  }

  private fun openModelInAR() {
    val intent = Intent(this, ARSceneActivity::class.java)
    intent.putExtra("model", poi.arModelName)
    intent.putExtra("poi", poi)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    startActivity(intent)
  }
}

