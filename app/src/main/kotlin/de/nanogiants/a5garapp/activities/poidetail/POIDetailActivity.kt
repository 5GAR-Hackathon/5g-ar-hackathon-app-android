package de.nanogiants.a5garapp.activities.poidetail

import android.media.Image
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import de.nanogiants.a5garapp.activities.poidetail.adapters.POIPhotoAdapter

import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityPoiDetailBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import timber.log.Timber

class POIDetailActivity : BaseActivity() {

  override val binding: ActivityPoiDetailBinding by viewBinding(ActivityPoiDetailBinding::inflate)

  lateinit var poiPhotoAdapter: POIPhotoAdapter

  lateinit var poiPhotoLayoutManager: GridLayoutManager


  override fun initView() {
    val poi = intent.getSerializableExtra("POI") as POI
    Timber.d("New activity $poi")

    binding.toolbar.title = poi.name
    binding.poiRatingBar.rating = 4f

    Glide.with(binding.poiBackgroundImageView)
      .load(if (poi.imageUrls.isEmpty()) "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg" else poi.imageUrls[0])
      .centerCrop()
      .into(binding.poiBackgroundImageView)

    poiPhotoLayoutManager = GridLayoutManager(this, 3)

    poiPhotoAdapter = POIPhotoAdapter()
    poiPhotoAdapter.addAll(poi.imageUrls)
    poiPhotoAdapter.onPhotoClicked = { imageUrl: String, index: Int, imageView: ImageView, ->
      StfalconImageViewer.Builder<String>(this@POIDetailActivity, poi.imageUrls) { view, image ->
        Glide.with(view)
          .load(image)
          .centerCrop()
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
  }
}