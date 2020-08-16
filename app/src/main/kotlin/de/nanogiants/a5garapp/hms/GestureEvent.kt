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
package de.nanogiants.a5garapp.hms

import android.view.MotionEvent

/**
 * Gesture event management class for storing and creating gestures.
 *
 * @author HW
 * @since 2019-06-13
 */
class GestureEvent private constructor() {
  var type = 0
    private set
  lateinit var eventFirst: MotionEvent
    private set
  lateinit var eventSecond: MotionEvent
    private set
  var distanceX = 0f
    private set
  var distanceY = 0f
    private set

  companion object {
    /**
     * Define the constant 0, indicating an unknown gesture type.
     */
    const val GESTURE_EVENT_TYPE_UNKNOW = 0

    /**
     * Define the constant 1, indicating that the gesture type is DOWN.
     */
    const val GESTURE_EVENT_TYPE_DOWN = 1

    /**
     * Define the constant 2, indicating that the gesture type is SINGLETAPUP.
     */
    const val GESTURE_EVENT_TYPE_SINGLETAPUP = 2

    /**
     * Define the constant 3, indicating that the gesture type is SCROLL.
     */
    const val GESTURE_EVENT_TYPE_SCROLL = 3

    /**
     * Create a gesture type: DOWN.
     *
     * @param motionEvent The gesture motion event: DOWN.
     * @return GestureEvent.
     */
    fun createDownEvent(motionEvent: MotionEvent): GestureEvent {
      val ret = GestureEvent()
      ret.type = GESTURE_EVENT_TYPE_DOWN
      ret.eventFirst = motionEvent
      return ret
    }

    /**
     * Create a gesture type: SINGLETAPUP.
     *
     * @param motionEvent The gesture motion event: SINGLETAPUP.
     * @return GestureEvent(SINGLETAPUP).
     */
    fun createSingleTapUpEvent(motionEvent: MotionEvent): GestureEvent {
      val ret = GestureEvent()
      ret.type = GESTURE_EVENT_TYPE_SINGLETAPUP
      ret.eventFirst = motionEvent
      return ret
    }

    /**
     * Create a gesture type: SCROLL.
     *
     * @param e1 The first down motion event that started the scrolling.
     * @param e2 The second down motion event that ended the scrolling.
     * @param distanceX The distance along the X axis that has been scrolled since the last call to onScroll.
     * @param distanceY The distance along the Y axis that has been scrolled since the last call to onScroll.
     * @return GestureEvent(SCROLL).
     */
    fun createScrollEvent(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): GestureEvent {
      val ret = GestureEvent()
      ret.type = GESTURE_EVENT_TYPE_SCROLL
      ret.eventFirst = e1
      ret.eventSecond = e2
      ret.distanceX = distanceX
      ret.distanceY = distanceY
      return ret
    }
  }
}