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
package de.nanogiants.a5garapp.hms.rendering

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import android.util.Log
import android.view.MotionEvent
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.huawei.hiar.ARCamera
import com.huawei.hiar.ARFrame
import com.huawei.hiar.ARHitResult
import com.huawei.hiar.ARLightEstimate.State.NOT_VALID
import com.huawei.hiar.ARPlane
import com.huawei.hiar.ARPlane.PlaneType.UNKNOWN_FACING
import com.huawei.hiar.ARPoint
import com.huawei.hiar.ARPoint.OrientationMode.ESTIMATED_SURFACE_NORMAL
import com.huawei.hiar.ARPose
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARTrackable.TrackingState.TRACKING
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.ARTestActivity
import de.nanogiants.a5garapp.hms.GestureEvent
import de.nanogiants.a5garapp.hms.common.ArDemoRuntimeException
import de.nanogiants.a5garapp.hms.common.DisplayRotationManager
import de.nanogiants.a5garapp.hms.common.TextDisplay
import de.nanogiants.a5garapp.hms.common.TextureDisplay
import java.util.ArrayList
import java.util.concurrent.ArrayBlockingQueue
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * This class provides rendering management related to the world scene, including
 * label rendering and virtual object rendering management.
 *
 * @author HW
 * @since 2020-03-21
 */
class WorldRenderManager(val mActivity: Activity) : Renderer {
  private var mSession: ARSession? = null

  private var frames = 0
  private var lastInterval: Long = 0
  private var fps = 0f
  private val mTextureDisplay: TextureDisplay = TextureDisplay()
  private val mTextDisplay: TextDisplay = TextDisplay()
  private val mLabelDisplay: LabelDisplay =
    LabelDisplay()
//  private val mObjectDisplay: ObjectDisplay =
//    ObjectDisplay()

  private lateinit var mDisplayRotationManager: DisplayRotationManager
  private lateinit var mQueuedSingleTaps: ArrayBlockingQueue<GestureEvent>

  /**
   * Set ARSession, which will update and obtain the latest data in OnDrawFrame.
   *
   * @param arSession ARSession.
   */
  fun setArSession(arSession: ARSession?) {
    if (arSession == null) {
      Log.e(TAG, "setSession error, arSession is null!")
      return
    }
    mSession = arSession
  }

  /**
   * Set a gesture type queue.
   *
   * @param queuedSingleTaps Gesture type queue.
   */
  fun setQueuedSingleTaps(queuedSingleTaps: ArrayBlockingQueue<GestureEvent>?) {
    if (queuedSingleTaps == null) {
      Log.e(TAG, "setSession error, arSession is null!")
      return
    }
    mQueuedSingleTaps = queuedSingleTaps
  }

  /**
   * Set the DisplayRotationManage object, which will be used in onSurfaceChanged and onDrawFrame.
   *
   * @param displayRotationManager DisplayRotationManage is a customized object.
   */
  fun setDisplayRotationManage(displayRotationManager: DisplayRotationManager?) {
    if (displayRotationManager == null) {
      Log.e(TAG, "SetDisplayRotationManage error, displayRotationManage is null!")
      return
    }
    mDisplayRotationManager = displayRotationManager
  }

  override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    // Set the window color.
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
    mTextureDisplay.init()
//    mTextDisplay.setListener { text, positionX, positionY -> showWorldTypeTextView(text, positionX, positionY) }
    mLabelDisplay.init(buildBitmaps(activity = mActivity as ARTestActivity))
//    mObjectDisplay.init(mActivity)
  }

  private fun buildBitmaps(activity: ARTestActivity): List<Bitmap> {
    return activity.provideViewList().map {
      getImageBitmap(it)
    }
  }

  /**
   * Create a thread for text display in the UI thread. This thread will be called back in TextureDisplay.
   *
   * @param text Gesture information displayed on the screen
   * @param positionX The left padding in pixels.
   * @param positionY The right padding in pixels.
   */
//  private fun showWorldTypeTextView(text: String?, positionX: Float, positionY: Float) {
//    mActivity.runOnUiThread {
//      mTextView.setTextColor(Color.WHITE)
//
//      // Set the font size to be displayed on the screen.
//      mTextView.textSize = 10f
//      if (text != null) {
//        mTextView.text = text
//        mTextView.setPadding(positionX.toInt(), positionY.toInt(), 0, 0)
//      } else {
//        mTextView.text = ""
//      }
//    }
//  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    mTextureDisplay.onSurfaceChanged(width, height)
    GLES20.glViewport(0, 0, width, height)
    mDisplayRotationManager.updateViewportRotation(width, height)
//    mObjectDisplay.setSize(width, height)
  }

  override fun onDrawFrame(unused: GL10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    if (mSession == null) {
      return
    }
    if (mDisplayRotationManager.getDeviceRotation()) {
      mDisplayRotationManager.updateArSessionDisplayGeometry(mSession)
    }
    try {
      mSession!!.setCameraTextureName(mTextureDisplay.getExternalTextureId())
      val arFrame = mSession!!.update()
      val arCamera = arFrame.camera

      // The size of the projection matrix is 4 * 4.
      val projectionMatrix = FloatArray(16)
      arCamera.getProjectionMatrix(projectionMatrix, PROJ_MATRIX_OFFSET, PROJ_MATRIX_NEAR, PROJ_MATRIX_FAR)
      mTextureDisplay.onDrawFrame(arFrame)
      val sb = StringBuilder()
      updateMessageData(sb)
      mTextDisplay.onDrawFrame(sb)

      // The size of ViewMatrix is 4 * 4.
      val viewMatrix = FloatArray(16)
      arCamera.getViewMatrix(viewMatrix, 0)
      for (plane in mSession!!.getAllTrackables(ARPlane::class.java)) {
        if (plane.type != UNKNOWN_FACING
          && plane.trackingState == TRACKING
        ) {
//          hideLoadingMessage()
          break
        }
      }
      mLabelDisplay.onDrawFrame(
        mSession!!.getAllTrackables(ARPlane::class.java), arCamera.displayOrientedPose,
        projectionMatrix
      )
//      handleGestureEvent(arFrame, arCamera, projectionMatrix, viewMatrix)
      val lightEstimate = arFrame.lightEstimate
      var lightPixelIntensity = 1f
      if (lightEstimate.state != NOT_VALID) {
        lightPixelIntensity = lightEstimate.pixelIntensity
      }
//      drawAllObjects(projectionMatrix, viewMatrix, lightPixelIntensity)
    } catch (e: ArDemoRuntimeException) {
      Log.e(TAG, "Exception on the ArDemoRuntimeException!")
    } catch (t: Throwable) {
      // This prevents the app from crashing due to unhandled exceptions.
      Log.e(TAG, "Exception on the OpenGL thread: ", t)
    }
  }

//  private fun drawAllObjects(projectionMatrix: FloatArray, viewMatrix: FloatArray, lightPixelIntensity: Float) {
//    val ite: MutableIterator<VirtualObject> = mVirtualObjects.iterator()
//    while (ite.hasNext()) {
//      val obj: VirtualObject = ite.next()
//      if (obj.getAnchor().getTrackingState() === STOPPED) {
//        ite.remove()
//      }
//      if (obj.getAnchor().getTrackingState() === TRACKING) {
////        mObjectDisplay.onDrawFrame(viewMatrix, projectionMatrix, lightPixelIntensity, obj)
//      }
//    }
//  }

  private val planeBitmaps: ArrayList<Bitmap?>
    get() {
      val bitmaps = ArrayList<Bitmap?>()
      bitmaps.add(getPlaneBitmap(R.id.dickbutt))
      bitmaps.add(getPlaneBitmap(R.id.dickbutt02))
      bitmaps.add(getPlaneBitmap(R.id.dickbutt03))
      return bitmaps
    }

  private fun getPlaneBitmap(id: Int): Bitmap? {
    val view = mActivity.findViewById<TextView>(id)
    view.isDrawingCacheEnabled = true
    view.measure(
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    var bitmap = view.drawingCache
    val matrix = Matrix()
    matrix.setScale(MATRIX_SCALE_SX, MATRIX_SCALE_SY)
    if (bitmap != null) {
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return bitmap
  }

  private fun getImageBitmap(imageView: ImageView): Bitmap {
    imageView.isDrawingCacheEnabled = true
    imageView.measure(
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    )
    imageView.layout(0, 0, imageView.measuredWidth, imageView.measuredHeight)
    var bitmap = imageView.drawingCache
    val matrix = Matrix()
    matrix.setScale(MATRIX_SCALE_SX, MATRIX_SCALE_SY)
    if (bitmap != null) {
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return bitmap
  }

  /**
   * Update the information to be displayed on the screen.
   *
   * @param sb String buffer.
   */
  private fun updateMessageData(sb: StringBuilder) {
    val fpsResult = doFpsCalculate()
    sb.append("FPS=").append(fpsResult).append(System.lineSeparator())
  }

  private fun doFpsCalculate(): Float {
    ++frames
    val timeNow = System.currentTimeMillis()

    // Convert millisecond to second.
    if ((timeNow - lastInterval) / 1000.0f > 0.5f) {
      fps = frames / ((timeNow - lastInterval) / 1000.0f)
      frames = 0
      lastInterval = timeNow
    }
    return fps
  }

//  private fun hideLoadingMessage() {
//    mActivity.runOnUiThread {
//      if (mSearchingTextView != null) {
//        mSearchingTextView!!.visibility = View.GONE
//        mSearchingTextView = null
//      }
//    }
//  }

//  private fun handleGestureEvent(
//    arFrame: ARFrame,
//    arCamera: ARCamera,
//    projectionMatrix: FloatArray,
//    viewMatrix: FloatArray
//  ) {
//    val event: Any = mQueuedSingleTaps!!.poll() ?: return
//
//    // Do not perform anything when the object is not tracked.
//    if (arCamera.trackingState != TRACKING) {
//      return
//    }
//    val eventType: Int = event.getType()
//    when (eventType) {
//      GestureEvent.GESTURE_EVENT_TYPE_DOWN -> {
//        doWhenEventTypeDown(viewMatrix, projectionMatrix, event)
//      }
//      GestureEvent.GESTURE_EVENT_TYPE_SCROLL -> {
//        if (mSelectedObj == null) {
//          break
//        }
//        val hitResult = hitTest4Result(arFrame, arCamera, event.getEventSecond())
//        if (hitResult != null) {
//          mSelectedObj.setAnchor(hitResult.createAnchor())
//        }
//      }
//      GestureEvent.GESTURE_EVENT_TYPE_SINGLETAPUP -> {
//
//        // Do not perform anything when an object is selected.
//        if (mSelectedObj != null) {
//          return
//        }
//        val tap: MotionEvent = event.getEventFirst()
//        var hitResult: ARHitResult? = null
//        hitResult = hitTest4Result(arFrame, arCamera, tap)
//        if (hitResult == null) {
//          break
//        }
//        doWhenEventTypeSingleTap(hitResult)
//      }
//      else -> {
//        Log.e(TAG, "Unknown motion event type, and do nothing.")
//      }
//    }
//  }

//  private fun doWhenEventTypeDown(viewMatrix: FloatArray, projectionMatrix: FloatArray, event: GestureEvent) {
//    if (mSelectedObj != null) {
//      mSelectedObj.setIsSelected(false)
//      mSelectedObj = null
//    }
//    for (obj in mVirtualObjects) {
//      if (mObjectDisplay.hitTest(viewMatrix, projectionMatrix, obj, event.getEventFirst())) {
//        obj.setIsSelected(true)
//        mSelectedObj = obj
//        break
//      }
//    }
//  }

//  private fun doWhenEventTypeSingleTap(hitResult: ARHitResult) {
//    // The hit results are sorted by distance. Only the nearest hit point is valid.
//    // Set the number of stored objects to 10 to avoid the overload of rendering and AR Engine.
//    if (mVirtualObjects.size >= 16) {
//      mVirtualObjects[0].getAnchor().detach()
//      mVirtualObjects.removeAt(0)
//    }
//    val currentTrackable = hitResult.trackable
//    if (currentTrackable is ARPoint) {
//      mVirtualObjects.add(VirtualObject(hitResult.createAnchor(), BLUE_COLORS))
//    } else if (currentTrackable is ARPlane) {
//      mVirtualObjects.add(VirtualObject(hitResult.createAnchor(), GREEN_COLORS))
//    } else {
//      Log.i(TAG, "Hit result is not plane or point.")
//    }
//  }

  private fun hitTest4Result(frame: ARFrame, camera: ARCamera, event: MotionEvent): ARHitResult? {
    var hitResult: ARHitResult? = null
    val hitTestResults = frame.hitTest(event)
    for (i in hitTestResults.indices) {
      // Determine whether the hit point is within the plane polygon.
      val hitResultTemp = hitTestResults[i] ?: continue
      val trackable = hitResultTemp.trackable
      val isPlanHitJudge = (trackable is ARPlane && trackable.isPoseInPolygon(hitResultTemp.hitPose)
          && calculateDistanceToPlane(hitResultTemp.hitPose, camera.pose) > 0)

      // Determine whether the point cloud is clicked and whether the point faces the camera.
      val isPointHitJudge = (trackable is ARPoint
          && trackable.orientationMode == ESTIMATED_SURFACE_NORMAL)

      // Select points on the plane preferentially.
      if (isPlanHitJudge || isPointHitJudge) {
        hitResult = hitResultTemp
        if (trackable is ARPlane) {
          break
        }
      }
    }
    return hitResult
  }

  companion object {
    private val TAG = WorldRenderManager::class.java.simpleName
    private const val PROJ_MATRIX_OFFSET = 0
    private const val PROJ_MATRIX_NEAR = 0.1f
    private const val PROJ_MATRIX_FAR = 100.0f
    private const val MATRIX_SCALE_SX = -1.0f
    private const val MATRIX_SCALE_SY = -1.0f
    private val BLUE_COLORS = floatArrayOf(66.0f, 133.0f, 244.0f, 255.0f)
    private val GREEN_COLORS = floatArrayOf(66.0f, 133.0f, 244.0f, 255.0f)

    /**
     * Calculate the distance between a point in a space and a plane. This method is used
     * to calculate the distance between a camera in a space and a specified plane.
     *
     * @param planePose ARPose of a plane.
     * @param cameraPose ARPose of a camera.
     * @return Calculation results.
     */
    private fun calculateDistanceToPlane(planePose: ARPose, cameraPose: ARPose): Float {
      // The dimension of the direction vector is 3.
      val normals = FloatArray(3)

      // Obtain the unit coordinate vector of a normal vector of a plane.
      planePose.getTransformedAxis(1, 1.0f, normals, 0)

      // Calculate the distance based on projection.
      return (cameraPose.tx() - planePose.tx()) * normals[0] // 0:x
      +(cameraPose.ty() - planePose.ty()) * normals[1] // 1:y
      +(cameraPose.tz() - planePose.tz()) * normals[2] // 2:z
    }
  }

  /**
   * The constructor passes context and activity. This method will be called when [Activity.onCreate].
   *
   * @param activity Activity
   * @param context Context
   */
//  init {
//    mSearchingTextView = mActivity.findViewById(R.id.searchingTextView)
//  }
}