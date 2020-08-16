/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nanogiants.a5garapp.hms.common

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.view.Display
import android.view.WindowManager
import com.huawei.hiar.ARSession
import timber.log.Timber

/**
 * Device rotation manager, which is used by the demo to adapt to device rotations
 *
 * @author HW
 * @since 2020-03-20
 */
class DisplayRotationManager(private val mContext: Context) : DisplayListener {
  /**
   * Check whether the current device is rotated.
   *
   * @return The device rotation result.
   */
  var deviceRotation = false
    private set
  private var mDisplay: Display? = null
  private var mViewPx = 0
  private var mViewPy = 0

  /**
   * Construct DisplayRotationManage with the context.
   *
   * @param context Context.
   */
  init {
    val systemService = mContext.getSystemService(WindowManager::class.java)
    mDisplay = systemService.defaultDisplay
  }

  /**
   * Register a listener on display changes. This method can be called when onResume is called for an activity.
   */
  fun registerDisplayListener() {
    val systemService = mContext.getSystemService(DisplayManager::class.java)
    systemService?.registerDisplayListener(this, null)
  }

  /**
   * Deregister a listener on display changes. This method can be called when onPause is called for an activity.
   */
  fun unregisterDisplayListener() {
    val systemService = mContext.getSystemService(DisplayManager::class.java)
    systemService?.unregisterDisplayListener(this)
  }

  /**
   * When a device is rotated, the viewfinder size and whether the device is rotated
   * should be updated to correctly display the geometric information returned by the
   * AR Engine. This method should be called when onSurfaceChanged.
   *
   * @param width Width of the surface updated by the device.
   * @param height Height of the surface updated by the device.
   */
  fun updateViewportRotation(width: Int, height: Int) {
    mViewPx = width
    mViewPy = height
    deviceRotation = true
  }

  /**
   * If the device is rotated, update the device window of the current ARSession.
   * This method can be called when onDrawFrame is called.
   *
   * @param session [ARSession] object.
   */
  fun updateArSessionDisplayGeometry(session: ARSession) {
    val displayRotation = mDisplay?.rotation ?: {
      Timber.e("updateArSessionDisplayGeometry mDisplay null!")
      0
    }()
    session.setDisplayGeometry(displayRotation, mViewPx, mViewPy)
    deviceRotation = false
  }

  override fun onDisplayAdded(displayId: Int) {}
  override fun onDisplayRemoved(displayId: Int) {}
  override fun onDisplayChanged(displayId: Int) {
    deviceRotation = true
  }
}