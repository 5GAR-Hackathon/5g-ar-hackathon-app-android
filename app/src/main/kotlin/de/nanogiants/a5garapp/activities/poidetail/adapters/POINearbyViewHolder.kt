/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.databinding.ItemNearbyBinding
import de.nanogiants.a5garapp.model.entities.domain.NearbyPOI
import de.nanogiants.a5garapp.model.entities.domain.Tag
import kotlin.math.roundToInt


class POINearbyViewHolder(val viewBinding: ItemNearbyBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: NearbyPOI) {
    viewBinding.nameTextView.text = item.name
    viewBinding.addressTextView.text = item.address
    viewBinding.distanceTextView.text = item.distance.roundToInt().toString() + "m"

    if (item.imageUrls.size > 0) {
      viewBinding.imageView2.load(item.imageUrls[0])
    } else {
      val tag = item.tags[0]
      val context = viewBinding.imageView2.context
      val id = context.resources.getIdentifier(
        getDrawableNameForTag(tag),
        "drawable",
        context.packageName
      )

      viewBinding.imageView2.setImageDrawable(ContextCompat.getDrawable(context, id))
    }
  }

  fun getDrawableNameForTag(tag: Tag): String? {
    return when (tag.id) {
      1001 -> "cafe_placeholder"
      1002 -> "bank_placeholder"
      else -> null
    }
  }
}
