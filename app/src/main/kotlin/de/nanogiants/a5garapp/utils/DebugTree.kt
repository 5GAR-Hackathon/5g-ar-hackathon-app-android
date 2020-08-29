/**
 * Created by appcom interactive GmbH on 29.08.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.utils

import timber.log.Timber

class DebugTree : Timber.DebugTree() {

  override fun createStackElementTag(element: StackTraceElement): String? =
    String.format(
      "%s %s:%s",
      super.createStackElementTag(element),
      element.methodName,
      element.lineNumber
    )
}
