/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import androidx.recyclerview.widget.RecyclerView
import coil.load
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.databinding.ItemTagBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.model.entities.domain.SelectableTag
import de.nanogiants.a5garapp.model.entities.domain.Tag
import java.util.Locale


class DashboardTagViewholder(val viewBinding: ItemTagBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {
  fun bind(item: SelectableTag) {
    viewBinding.chipTagTextView.text = item.name.capitalize(Locale.getDefault())
    viewBinding.chipTagTextView.isSelected = item.selected
  }
}
