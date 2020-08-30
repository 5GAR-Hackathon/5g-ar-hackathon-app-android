package de.nanogiants.a5garapp.activities.poidetail

import android.content.res.ColorStateList
import android.media.Image
import android.text.Html
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIPhotoAdapter
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIReviewAdapter

import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityPoiDetailBinding
import de.nanogiants.a5garapp.model.datastore.ReviewDatastore
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.utils.JSONReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class POIDetailActivity : BaseActivity() {

  override val binding: ActivityPoiDetailBinding by viewBinding(ActivityPoiDetailBinding::inflate)

  lateinit var poiPhotoAdapter: POIPhotoAdapter

  lateinit var poiPhotoLayoutManager: GridLayoutManager

  lateinit var poiReviewAdapter: POIReviewAdapter

  lateinit var poiReviewLayoutManager: LinearLayoutManager

  @Inject
  lateinit var reviewDatastore: ReviewDatastore

  override fun initView() {
    val poi = intent.getSerializableExtra("POI") as POI
    Timber.d("New activity $poi")

    binding.toolbar.title = poi.name
    binding.poiRatingBar.rating = 4f
    binding.descriptionTextView.text = poi.description

    Glide.with(binding.poiBackgroundImageView)
      .load(if (poi.imageUrls.isEmpty()) "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg" else poi.imageUrls[0])
      .centerCrop()
      .into(binding.poiBackgroundImageView)

    poiPhotoLayoutManager = GridLayoutManager(this, 4)

    poiPhotoAdapter = POIPhotoAdapter()
    poiPhotoAdapter.addAll(poi.imageUrls)
    poiPhotoAdapter.onPhotoClicked = { imageUrl: String, index: Int, imageView: ImageView, ->
      StfalconImageViewer.Builder<String>(this@POIDetailActivity, poi.imageUrls) { view, image ->
        Glide.with(view)
          .load(image)
          .centerInside()
          .into(view)
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

    poi.tags.forEach {
      val chip = Chip(this@POIDetailActivity)
      chip.text = it.name.capitalize(Locale.getDefault())
      chip.setTextAppearanceResource(R.style.TagChipTextStyle)
      chip.setChipBackgroundColorResource(R.color.white)

      chip.isClickable = false
      chip.isCheckable = false

      binding.tagChipGroup.addView(chip)
    }
  }
}