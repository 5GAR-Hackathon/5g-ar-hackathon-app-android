/**
 * Created by appcom interactive GmbH on 31.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.favorites.adapters

import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.databinding.ItemFavoritesBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import java.util.Locale


class FavoritesViewHolder(val viewBinding: ItemFavoritesBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: POI) {
    viewBinding.poiNameTextView.text = item.name
    viewBinding.poiTagTextView.text =
      item.tags.map { it.name.capitalize(Locale.getDefault()) }.joinToString(", ")
    viewBinding.poiRatingBar.rating = item.rating

    viewBinding.poiImageView.load(
      if (item.imageUrls.isEmpty()) {
        "https://www.aekno.de/fileadmin/_processed_/1/6/csm_ks-duesseldorf-01_a8b7d2779a.jpg"
      } else {
        item.imageUrls[0]
      }
    )
  }
}