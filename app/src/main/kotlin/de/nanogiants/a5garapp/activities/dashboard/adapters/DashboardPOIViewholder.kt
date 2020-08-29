/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.model.entities.domain.POI


class DashboardPOIViewholder(val viewBinding: ItemDashboardPoiBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {
  fun bind(item: POI) {
    viewBinding.textView.text = item.name
  }
}
