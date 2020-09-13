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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import android.os.Build.HARDWARE
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.huawei.hiar.ARCamera
import com.huawei.hiar.ARFrame
import com.huawei.hiar.ARHitResult
import com.huawei.hiar.ARPlane
import com.huawei.hiar.ARPlane.PlaneType.UNKNOWN_FACING
import com.huawei.hiar.ARPoint
import com.huawei.hiar.ARPoint.OrientationMode.ESTIMATED_SURFACE_NORMAL
import com.huawei.hiar.ARPose
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARTrackable.TrackingState.TRACKING
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.ar.ARTestActivity
import de.nanogiants.a5garapp.hms.Banner
import de.nanogiants.a5garapp.hms.GestureEvent
import de.nanogiants.a5garapp.hms.common.ArDemoRuntimeException
import de.nanogiants.a5garapp.hms.common.DisplayRotationManager
import de.nanogiants.a5garapp.hms.common.TextDisplay
import de.nanogiants.a5garapp.hms.common.TextureDisplay
import okhttp3.internal.wait
import timber.log.Timber
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
class WorldRenderManager(private val mActivity: Activity) : Renderer {
  private var mSession: ARSession? = null

  private var frames = 0
  private var lastInterval: Long = 0
  private var fps = 0f
  private val mTextureDisplay: TextureDisplay = TextureDisplay()
  private val mTextDisplay: TextDisplay = TextDisplay()
  private val mLabelDisplay: LabelDisplay = LabelDisplay()
//  private val mObjectDisplay: ObjectDisplay =
//    ObjectDisplay()

  private lateinit var mDisplayRotationManager: DisplayRotationManager
  private lateinit var mQueuedSingleTaps: ArrayBlockingQueue<GestureEvent>

  private val bannerList: MutableList<Banner> = mutableListOf()

  private val textView: TextView = mActivity.findViewById(R.id.wordTextView)

  /**
   * Set ARSession, which will update and obtain the latest data in OnDrawFrame.
   *
   * @param arSession ARSession.
   */
  fun setArSession(arSession: ARSession?) {
    if (arSession == null) {
      Timber.e("setSession error, arSession is null!")
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
      Timber.e("setSession error, arSession is null!")
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
      Timber.e("SetDisplayRotationManage error, displayRotationManage is null!")
      return
    }
    mDisplayRotationManager = displayRotationManager
  }

  override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    // Set the window color.
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
    mTextureDisplay.init()
    /*mTextDisplay.setListener { text: String, positionX: Float, positionY: Float ->
      showWorldTypeTextView(
        text,
        positionX,
        positionY
      )
    }*/
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
  private fun showWorldTypeTextView(text: String?, positionX: Float, positionY: Float) {
    mActivity.runOnUiThread {
      textView.setTextColor(Color.WHITE)

      // Set the font size to be displayed on the screen.
      textView.textSize = 10f
      if (text != null) {
        textView.text = text
        textView.setPadding(positionX.toInt(), positionY.toInt(), 0, 0)
      } else {
        textView.text = ""
      }
    }
  }

  override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
    mTextureDisplay.onSurfaceChanged(width, height)
    GLES20.glViewport(0, 0, width, height)
    mDisplayRotationManager.updateViewportRotation(width, height)
//    mObjectDisplay.setSize(width, height)
  }

  override fun onDrawFrame(unused: GL10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

    mSession?.let {
      if (mDisplayRotationManager.deviceRotation) {
        mDisplayRotationManager.updateArSessionDisplayGeometry(it)
      }
      try {
        it.setCameraTextureName(mTextureDisplay.externalTextureId)
        val arFrame = it.update()
        val arCamera = arFrame.camera

        // The size of the projection matrix is 4 * 4.
        val projectionMatrix = FloatArray(16)
        arCamera.getProjectionMatrix(
          projectionMatrix,
          PROJ_MATRIX_OFFSET,
          PROJ_MATRIX_NEAR,
          PROJ_MATRIX_FAR
        )
        mTextureDisplay.onDrawFrame(arFrame)
        val sb = StringBuilder()
        updateMessageData(sb)
        mTextDisplay.onDrawFrame(sb)

        // The size of ViewMatrix is 4 * 4.
        val viewMatrix = FloatArray(16)
        arCamera.getViewMatrix(viewMatrix, 0)
        for (plane in it.getAllTrackables(ARPlane::class.java)) {
          Timber.d("FOund plane $plane")


          if (plane.type != UNKNOWN_FACING
            && plane.trackingState == TRACKING
          ) {
//          hideLoadingMessage()
            break
          }
        }
        /*val planes = it.getAllTrackables(ARPlane::class.java)
        //.filter { item -> item.label == ARPlane.SemanticPlaneLabel.PLANE_FLOOR }
        mLabelDisplay.onDrawFrame(
          planes, arCamera.displayOrientedPose,
          projectionMatrix
        )*/

        handleGestureEvent(arFrame, arCamera, projectionMatrix, viewMatrix)
//        val lightEstimate = arFrame.lightEstimate
//        var lightPixelIntensity = if (lightEstimate.state == NOT_VALID) {
//          1f
//        } else {
//          lightEstimate.pixelIntensity
//        }
//        drawAllObjects(projectionMatrix, viewMatrix, lightPixelIntensity)
        drawBannerList(arCamera.displayOrientedPose, projectionMatrix)
      } catch (e: ArDemoRuntimeException) {
        Timber.e("Exception on the ArDemoRuntimeException!")
      } catch (t: Throwable) {
        // This prevents the app from crashing due to unhandled exceptions.
        Timber.e(t)
      }
    }
  }

  private fun drawBannerList(displayOrientedPose: ARPose, projectionMatrix: FloatArray) {
    mLabelDisplay.onDrawBannerList(
      bannerList.filter { it.anchor.trackingState === TRACKING },
      displayOrientedPose,
      projectionMatrix
    )
  }

//
//  private fun getPlaneBitmap(id: Int): Bitmap? {
//    val view = mActivity.findViewById<TextView>(id)
//    view.isDrawingCacheEnabled = true
//    view.measure(
//      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//    )
//    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//    var bitmap = view.drawingCache
//    val matrix = Matrix()
//    matrix.setScale(MATRIX_SCALE_SX, MATRIX_SCALE_SY)
//    if (bitmap != null) {
//      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//    }
//    return bitmap
//  }

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
//        mSearchingTextView.visibility = View.GONE
//        mSearchingTextView = null
//      }
//    }
//  }

  private fun handleGestureEvent(
    arFrame: ARFrame,
    arCamera: ARCamera,
    projectionMatrix: FloatArray,
    viewMatrix: FloatArray
  ) {
    val event: GestureEvent = mQueuedSingleTaps.poll() ?: return
    Timber.d("Gesture $event")

    // Do not perform anything when the object is not tracked.
    if (arCamera.trackingState != TRACKING) {
      return
    }
    when (event.type) {
      GestureEvent.GESTURE_EVENT_TYPE_DOWN -> {
//s        doWhenEventTypeDown(viewMatrix, projectionMatrix, event)
      }
//      GestureEvent.GESTURE_EVENT_TYPE_SCROLL -> {
//        if (mSelectedObj == null) {
//          break
//        }
//        val hitResult = hitTest4Result(arFrame, arCamera, event.getEventSecond())
//        if (hitResult != null) {
//          mSelectedObj.setAnchor(hitResult.createAnchor())
//        }
//      }
      GestureEvent.GESTURE_EVENT_TYPE_SINGLETAPUP -> {

        // Do not perform anything when an object is selected.
//        if (mSelectedObj != null) {
//          return
//        }
        val tap: MotionEvent = event.eventFirst
        val hitResult: ARHitResult = hitTest4Result(arFrame, arCamera, tap) ?: return
        doWhenEventTypeSingleTap(hitResult)
      }
      else -> {
        Timber.e("Unknown motion event type, and do nothing.")
      }
    }
  }

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

  private fun doWhenEventTypeSingleTap(hitResult: ARHitResult) {
    // The hit results are sorted by distance. Only the nearest hit point is valid.
    // Set the number of stored objects to 10 to avoid the overload of rendering and AR Engine.
//    if (mVirtualObjects.size >= 16) {
//      mVirtualObjects[0].getAnchor().detach()
//      mVirtualObjects.removeAt(0)
//    }
    Timber.d("Hit a result??? ${hitResult.trackable}")
    when (hitResult.trackable) {
      is ARPoint -> {
        // TODO: 16.08.2020
//        mVirtualObjects.add(VirtualObject(hitResult.createAnchor(), BLUE_COLORS))
      }
      is ARPlane -> {
        Timber.d("Hit a result!")

        // if (bannerList.size == 1) return
        val bitmap = drawTextToBitmap(
          textView.context,
          "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet"
        )

        /*val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(110, 110, 110)
        paint.textSize = 12f
        //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)

// draw text to the Canvas center

// draw text to the Canvas center
        val text = "HelloWorld!!!"
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x: Float = (bitmap.width - bounds.width()) / 6f
        val y: Float = (bitmap.height + bounds.height()) / 5f

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.drawText(text, x, y, paint)*/


        // dc.setText(DrawingConfig.TextValues("Hello World", 8))
        // dc.setBackground(bitmap)
        // dc.setPaint(Paint(Paint.ANTI_ALIAS_FLAG))

        // val newBitmap = dc.draw()

        bitmap?.rotate(180f)
        bannerList.add(Banner(hitResult.createAnchor(), bitmap!!))

        Timber.d("We added a dickbutt")
        // TODO: 16.08.2020
//        mVirtualObjects.add(VirtualObject(hitResult.createAnchor(), GREEN_COLORS))
      }
      else -> {
        Timber.i("Hit result is not plane or point.")
      }
    }
  }

  private fun drawTextToBitmap(context: Context, mText: String): Bitmap? {
    return try {
      val scale: Float = context.resources.getDisplayMetrics().density
      var bitmap = BitmapFactory.decodeResource(mActivity.resources, R.drawable.untitled)
      bitmap.setHasAlpha(false)

      var bitmapConfig = bitmap.config

      if (bitmapConfig == null) {
        bitmapConfig = Bitmap.Config.HARDWARE
      }
      // resource bitmaps are imutable,
      // so we need to convert it to mutable one
      bitmap = bitmap.copy(bitmapConfig, true)
      val canvas = Canvas(bitmap)
      // new antialised Paint
      val paint = Paint(Paint.ANTI_ALIAS_FLAG)
      // text color - #3D3D3D
      paint.color = Color.rgb(1f, 1f, 1f)
      // text size in pixels
      paint.textSize = (36 * scale)
      // text shadow
      paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)

      val padding = 8
      val textLayout = StaticLayout(
        mText,
        TextPaint(paint),
        canvas.width - (64f * scale).toInt(),
        Layout.Alignment.ALIGN_CENTER,
        1f,
        0f,
        false
      )

      // draw text to the Canvas center
      val bounds = Rect().apply {
        top = 20
        left = 20
        right = canvas.width - padding
        bottom = textLayout.height
      }

      paint.getTextBounds(mText, 0, mText.length, bounds)

      val x = (bitmap.width - bounds.width()) / 2f
      val y = (bitmap.height + bounds.height()) / 2f

      canvas.save()
      //canvas.translate(x, y)
      canvas.translate(32f * scale, 32f * scale)
      textLayout.draw(canvas)
      canvas.restore()
      //canvas.drawText(mText, x * scale, y * scale, paint)


      bitmap
    } catch (e: Exception) {
      Timber.e("ERRRRROR $e")
      null
    }
  }

  fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
  }

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
      return (cameraPose.tx() - planePose.tx()) * normals[0] + // 0:x
          (cameraPose.ty() - planePose.ty()) * normals[1] + // 1:y
          (cameraPose.tz() - planePose.tz()) * normals[2] // 2:z
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