/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import timber.log.Timber
import java.util.Locale


class DashboardPOIViewholder(val viewBinding: ItemDashboardPoiBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {
  fun bind(item: POI) {
    viewBinding.upvotesTextView.text = item.upvotes.toString()
    viewBinding.downvotesTextView.text = item.downvotes.toString()

    viewBinding.nameTextView.text = item.name
    viewBinding.tagsTextView.text =
      item.tags.joinToString(", ") { it.name.capitalize(Locale.getDefault()) }

    viewBinding.backgroundImageView.load(
      if (item.images.isEmpty()) {
        "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg"
      } else {
        item.images[0].url
      }
    )
  }
}
