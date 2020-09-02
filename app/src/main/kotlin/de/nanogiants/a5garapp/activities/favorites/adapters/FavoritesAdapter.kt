/**
 * Created by appcom interactive GmbH on 31.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.favorites.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemFavoritesBinding
import de.nanogiants.a5garapp.model.entities.domain.POI


class FavoritesAdapter : RecyclerView.Adapter<FavoritesViewHolder>() {

  private var items: MutableList<POI> = mutableListOf()
  var onPOIClicked: ((POI, View) -> Unit) = { poi: POI, view: View -> }

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<POI>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  fun clear() {
    items.clear()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoritesViewHolder(
    ItemFavoritesBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
    holder.bind(items.get(position))
    holder.viewBinding.rootView.setOnClickListener {
      onPOIClicked(items.get(position), holder.viewBinding.poiImageView)
    }

  }

}