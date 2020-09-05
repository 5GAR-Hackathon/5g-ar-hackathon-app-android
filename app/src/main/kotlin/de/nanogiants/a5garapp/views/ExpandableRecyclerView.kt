/**
 * Created by appcom interactive GmbH on 05.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import de.nanogiants.a5garapp.R.anim
import timber.log.Timber

class ExpandableRecyclerView : RecyclerView {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  private var isExpanded: Boolean = true

  fun expand(skipAnimation: Boolean = false) {
    if (!isExpanded) {
      val slide = AnimationUtils.loadAnimation(context, anim.enter_from_top)
      slide.duration = if (skipAnimation) 0 else 300
      startAnimation(slide);

      isExpanded = true
    }
  }

  fun collapse(skipAnimation: Boolean = false) {
    if (isExpanded) {
      val slide = AnimationUtils.loadAnimation(context, anim.exit_from_bottom)
      slide.duration = if (skipAnimation) 0 else 300
      startAnimation(slide);

      isExpanded = false
    }
  }

  fun toggle() {
    Timber.d("Currently $isExpanded")

    if (isExpanded) collapse() else expand()
  }
}