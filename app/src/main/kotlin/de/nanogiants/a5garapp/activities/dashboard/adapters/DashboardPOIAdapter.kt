/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.model.entities.domain.POI


class DashboardPOIAdapter : RecyclerView.Adapter<DashboardPOIViewholder>() {

  private var items: MutableList<POI> = mutableListOf()
  var onPOIClicked: ((POI) -> Unit) = {}

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<POI>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DashboardPOIViewholder(
    ItemDashboardPoiBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: DashboardPOIViewholder, position: Int) {
    holder.bind(items.get(position))
    holder.viewBinding.rootView.setOnClickListener {
      onPOIClicked(items.get(position))
    }

  }
}