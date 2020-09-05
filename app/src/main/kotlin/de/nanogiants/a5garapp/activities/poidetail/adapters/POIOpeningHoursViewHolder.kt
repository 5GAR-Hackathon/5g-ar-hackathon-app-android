/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.activities.poidetail.adapters

import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.databinding.ItemOpeninghoursBinding
import de.nanogiants.a5garapp.databinding.ItemReviewBinding
import de.nanogiants.a5garapp.model.entities.domain.OpeningHour
import de.nanogiants.a5garapp.model.entities.domain.Review
import java.util.Locale


class POIOpeningHoursViewHolder(val viewBinding: ItemOpeninghoursBinding) :
  RecyclerView.ViewHolder(viewBinding.root) {

  fun bind(item: OpeningHour) {
    viewBinding.openingHourDayTextView.text = item.day
    viewBinding.openingHoursHoursTextView.text = item.hours
  }
}
