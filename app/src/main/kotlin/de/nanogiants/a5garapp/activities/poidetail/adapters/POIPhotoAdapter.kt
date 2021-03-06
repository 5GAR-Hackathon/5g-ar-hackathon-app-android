/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemPhotoBinding
import de.nanogiants.a5garapp.model.entities.domain.Image


class POIPhotoAdapter : RecyclerView.Adapter<POIPhotoViewHolder>() {

  private var items: MutableList<Image> = mutableListOf()
  var onPhotoClicked: ((Image, Int, ImageView) -> Unit) = { _: Image, _: Int, _: ImageView -> }

  override fun getItemCount() = items.size

  fun addAll(newItems: Collection<Image>) {
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = POIPhotoViewHolder(
    ItemPhotoBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: POIPhotoViewHolder, position: Int) {
    holder.bind(items[position])
    holder.viewBinding.rootView.setOnClickListener {
      onPhotoClicked(items[position], position, holder.viewBinding.imageView)
    }
  }
}