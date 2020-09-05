/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemDashboardPoiBinding
import de.nanogiants.a5garapp.databinding.ItemTagBinding
import de.nanogiants.a5garapp.model.entities.domain.SelectableTag
import de.nanogiants.a5garapp.model.entities.domain.Tag


class DashboardTagAdapter : RecyclerView.Adapter<DashboardTagViewholder>() {

  private var items: MutableList<SelectableTag> = mutableListOf()

  var onTagClicked: ((SelectableTag, Int) -> Unit) = { selectableTag: SelectableTag, i: Int -> }

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<SelectableTag>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  fun clear() {
    items.clear()
    notifyDataSetChanged()
  }

  fun get(position: Int): SelectableTag = items.get(position)

  fun selectTag(position: Int, select: Boolean) {
    get(position).selected = select
    notifyItemChanged(position)
  }

  fun getAll(): List<SelectableTag> = items

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DashboardTagViewholder(
    ItemTagBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: DashboardTagViewholder, position: Int) {
    holder.bind(items[position])
    holder.viewBinding.rootView.setOnClickListener {
      onTagClicked(items[position], position)
    }
  }
}