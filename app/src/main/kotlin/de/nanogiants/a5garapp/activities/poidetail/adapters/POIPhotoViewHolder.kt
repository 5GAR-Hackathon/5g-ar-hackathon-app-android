/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.databinding.ItemPhotoBinding


class POIPhotoViewHolder(val viewBinding: ItemPhotoBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: String) {
    viewBinding.imageView.load(item)
  }
}
