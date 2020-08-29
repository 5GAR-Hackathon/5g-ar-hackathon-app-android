/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.databinding.ItemPhotoBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import java.util.Locale


class POIPhotoViewHolder(val viewBinding: ItemPhotoBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: String) {
    Glide.with(viewBinding.imageView)
      .load(item)
      .centerCrop()
      .into(viewBinding.imageView)
  }
}
