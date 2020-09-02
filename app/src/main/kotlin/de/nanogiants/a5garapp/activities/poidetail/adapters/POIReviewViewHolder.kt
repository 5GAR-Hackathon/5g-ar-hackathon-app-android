/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemReviewBinding
import de.nanogiants.a5garapp.model.entities.domain.Review
import java.util.Locale


class POIReviewViewHolder(val viewBinding: ItemReviewBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: Review) {
    viewBinding.contentTextView.text = item.content
    viewBinding.userNameTextView.text =
      item.userName.split('_').joinToString(" ") { it.capitalize(Locale.getDefault()) }
    viewBinding.createdAtTextView.text = item.createdAt
    viewBinding.reviewLikeCountTextView.text = item.likeCount.toString()
    viewBinding.poiReviewRatingBar.rating = item.rating

    viewBinding.userImageView.hash = item.userName.hashCode()
  }
}
