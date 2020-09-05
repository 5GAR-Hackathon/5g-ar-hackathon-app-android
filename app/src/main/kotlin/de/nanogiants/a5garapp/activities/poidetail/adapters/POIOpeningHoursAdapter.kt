/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemOpeninghoursBinding
import de.nanogiants.a5garapp.databinding.ItemReviewBinding
import de.nanogiants.a5garapp.model.entities.domain.OpeningHour
import de.nanogiants.a5garapp.model.entities.domain.Review


class POIOpeningHoursAdapter : RecyclerView.Adapter<POIOpeningHoursViewHolder>() {

  private var items: MutableList<OpeningHour> = mutableListOf()

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<OpeningHour>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = POIOpeningHoursViewHolder(
    ItemOpeninghoursBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: POIOpeningHoursViewHolder, position: Int) {
    holder.bind(items[position])
  }
}