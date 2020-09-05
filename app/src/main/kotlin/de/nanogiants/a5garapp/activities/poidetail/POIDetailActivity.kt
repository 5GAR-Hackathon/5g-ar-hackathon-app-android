package de.nanogiants.a5garapp.activities.poidetail

import android.app.FragmentManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.chip.Chip
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMapOptions
import com.huawei.hms.maps.OnMapReadyCallback
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIOpeningHoursAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIPhotoAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIReviewAdapter
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.SharedPreferencesController
import de.nanogiants.a5garapp.databinding.ActivityPoiDetailBinding
import de.nanogiants.a5garapp.model.datastore.ReviewDatastore
import de.nanogiants.a5garapp.model.entities.domain.POI
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

  @Inject
  lateinit var reviewDatastore: ReviewDatastore

  @Inject
  lateinit var sharedPreferencesController: SharedPreferencesController

  lateinit var poi: POI

  lateinit var mapFragment: POIMapFragment

  lateinit
  var optionsMenu: Menu

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
  }

  override fun initView() {
    poi = intent.getSerializableExtra("POI") as POI
    Timber.d("New activity $poi")

    binding.toolbar.title = poi.name
    binding.poiRatingBar.rating = 4f
    binding.descriptionTextView.text = poi.description

    binding.poiBackgroundImageView.load(
      if (poi.imageUrls.isEmpty()) {
        "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg"
      } else {
        poi.imageUrls[0]
      }
    )

    poiPhotoLayoutManager = GridLayoutManager(this, 4)

    poiPhotoAdapter = POIPhotoAdapter()
    poiPhotoAdapter.addAll(poi.imageUrls)
    poiPhotoAdapter.onPhotoClicked = { _: String, index: Int, imageView: ImageView ->
      StfalconImageViewer.Builder(this@POIDetailActivity, poi.imageUrls) { view, image ->
        view.scaleType = ScaleType.FIT_CENTER
        view.load(image)
      }
        .withTransitionFrom(imageView)
        .withHiddenStatusBar(false)
        .withStartPosition(index)
        .show()
    }

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

    binding.reviewLabelTextView.text = "Reviews (${poi.reviews.size})"
    binding.ratingTextView.text = "${poi.reviews.size} Reviews"
    binding.poiRatingBar.rating = poi.rating

    poiOpeningHoursLayoutManager = LinearLayoutManager(this)
    poiOpeningHoursAdapter = POIOpeningHoursAdapter()
    poiOpeningHoursAdapter.addAll(poi.openingHours)

    binding.openingHoursConstraintLayout.visibility =
      if (poi.openingHours.isEmpty()) View.GONE else View.VISIBLE
    binding.openingHoursRecyclerView.apply {
      layoutManager = poiOpeningHoursLayoutManager
      adapter = poiOpeningHoursAdapter
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
        true
      }
      R.id.bookmark -> {
        lifecycleScope.launch {
          sharedPreferencesController.bookmarkPOI(poi)
          updateBookmarkOptionsIcon()
        }
        return true
      }
      android.R.id.home -> {
        ActivityCompat.finishAfterTransition(this)
        true
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
    mapFragment.enableInteractiveMap(false)

    mapFragment.clearPOIs()
    mapFragment.addPOI(poi)
  }
}