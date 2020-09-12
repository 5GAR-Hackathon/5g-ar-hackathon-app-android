/**
 * Created by appcom interactive GmbH on 12.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.utils

import android.content.Context

class Utilities {
  companion object {
    val PACKAGE_DRAWABLE = "drawable"
    val PACKAGE_RAW = "raw"

    fun getResourceId(packageName: String, resourceName: String, context: Context): Int {
      return context.resources.getIdentifier(
        resourceName,
        packageName,
        context.packageName
      )
    }
  }
}