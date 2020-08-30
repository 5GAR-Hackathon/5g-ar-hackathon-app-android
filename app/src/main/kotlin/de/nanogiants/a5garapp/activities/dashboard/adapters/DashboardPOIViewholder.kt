/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import java.util.Locale


class DashboardPOIViewholder(val viewBinding: ItemDashboardPoiBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {
  fun bind(item: POI) {
    viewBinding.poiReviewRatingBar.rating = item.rating
    viewBinding.nameTextView.text = item.name
    viewBinding.tagsTextView.text =
      item.tags.map { it.name.capitalize(Locale.getDefault()) }.joinToString(", ")

    Glide.with(viewBinding.backgroundImageView)
      .load(if (item.imageUrls.isEmpty()) "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg" else item.imageUrls[0])
      .centerCrop()
      .into(viewBinding.backgroundImageView)
  }
}
