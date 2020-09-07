/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemNearbyBinding
import de.nanogiants.a5garapp.databinding.ItemOpeninghoursBinding
import de.nanogiants.a5garapp.databinding.ItemReviewBinding
import de.nanogiants.a5garapp.model.entities.domain.NearbyPOI
import de.nanogiants.a5garapp.model.entities.domain.OpeningHour
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.Review


class POINearbyAdapter : RecyclerView.Adapter<POINearbyViewHolder>() {

  private var items: MutableList<NearbyPOI> = mutableListOf()
  var onNearbyPOIClicked: ((String) -> Unit) = {}

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<NearbyPOI>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): POINearbyViewHolder {
    val binding = ItemNearbyBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )

    binding.rootView.layoutParams =
      ViewGroup.LayoutParams((parent.width * 0.7).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
    return POINearbyViewHolder(binding)
  }

  override fun onBindViewHolder(holder: POINearbyViewHolder, position: Int) {
    holder.bind(items[position])
    holder.viewBinding.rootView.setOnClickListener {
      onNearbyPOIClicked(items[position].url)
    }
  }
}