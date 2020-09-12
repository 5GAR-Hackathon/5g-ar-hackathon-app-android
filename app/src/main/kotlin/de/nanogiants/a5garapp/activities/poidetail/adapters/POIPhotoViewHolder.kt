/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.databinding.ItemPhotoBinding
import de.nanogiants.a5garapp.model.entities.domain.Image
import de.nanogiants.a5garapp.model.entities.domain.ImageType.NORMAL
import de.nanogiants.a5garapp.model.entities.domain.ImageType.PANORAMA
import de.nanogiants.a5garapp.utils.Utilities


class POIPhotoViewHolder(val viewBinding: ItemPhotoBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: Image) {
    if (item.type == PANORAMA) {
      viewBinding.textGuard.visibility = View.VISIBLE
      viewBinding.panoramaIndicatorTextView.visibility = View.VISIBLE
    } else {
      viewBinding.textGuard.visibility = View.GONE
      viewBinding.panoramaIndicatorTextView.visibility = View.GONE
    }

    if (item.type == NORMAL) {
      viewBinding.imageView.load(item.url)
    } else {
      val id =
        Utilities.getResourceId(Utilities.PACKAGE_RAW, item.url, viewBinding.imageView.context)
      val uri =
        Uri.parse("android.resource://" + viewBinding.imageView.context.packageName + "/" + id);

      viewBinding.imageView.load(uri)
    }
  }
}
