/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.dashboard.adapters

import androidx.core.content.ContextCompat
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
    viewBinding.chipRootView.isSelected = item.selected

    val context = viewBinding.tagIconImageView.context
    val id = context.resources.getIdentifier(
      getDrawableNameForTag(item.id, item.selected),
      "drawable",
      context.packageName
    )
    viewBinding.tagIconImageView.setImageDrawable(ContextCompat.getDrawable(context, id))
  }

  private fun getDrawableNameForTag(tagId: Int, selected: Boolean): String {
    val name = when (tagId) {
      1 -> "ic_coffee" // restaurant

      2 -> "ic_map_outline" // place

      3 -> "ic_image_outline" // museum

      4 -> "ic_color_palette_outline" // art

      5 -> "ic_speaker" // concert

      6 -> "ic_flag" // kiosk

      7 -> "ic_flag" // bar

      8 -> "ic_flag" // zoo

      9 -> "ic_music_outline" // music

      10 -> "ic_anchor" // water

      11 -> "ic_film_outline" // theater

      12 -> "ic_resource_package" // startup

      13 -> "ic_briefcase_outline" // business

      14 -> "ic_flag" // lookout

      15 -> "ic_home_outline__1_" // architecture

      16 -> "ic_flag" // hotel

      17 -> "ic_flag" // historic
      else -> "ic_flag"
    }

    return "$name${if (selected) "_selected" else ""}"
  }
}
