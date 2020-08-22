package de.nanogiants.a5garapp.activities

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.ImageView
import com.huawei.hiar.ARConfigBase
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARWorldTrackingConfig
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityArTestBinding
import de.nanogiants.a5garapp.hms.GestureEvent
import de.nanogiants.a5garapp.hms.common.DisplayRotationManager
import de.nanogiants.a5garapp.hms.rendering.WorldRenderManager
import timber.log.Timber
import java.util.concurrent.ArrayBlockingQueue

class ARTestActivity : BaseActivity() {

  private val rotationManager by lazy {
    DisplayRotationManager(this)
  }
  private val worldRenderManager by lazy {
    WorldRenderManager(this)
  }

  private var arSession: ARSession? = null
  private val tapQueue = ArrayBlockingQueue<GestureEvent>(2)
  lateinit var detector: GestureDetector

  override val binding: ActivityArTestBinding by viewBinding(ActivityArTestBinding::inflate)

  @SuppressLint("ClickableViewAccessibility")
  override fun initView() {
    // TODO: 15.08.2020 chech ar enging ability @see WorldActivity.java in ar sample project

    binding.surfaceView.apply {
      preserveEGLContextOnPause = true
      setEGLContextClientVersion(2)
      setEGLConfigChooser(8, 8, 8, 8, 16, 0)
      setRenderer(worldRenderManager)
      renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    worldRenderManager.apply {
      setDisplayRotationManage(rotationManager)
      setQueuedSingleTaps(tapQueue)
    }

    detector = GestureDetector(this, object : SimpleOnGestureListener() {
      override fun onSingleTapUp(e: MotionEvent): Boolean {
        onGestureEvent(GestureEvent.createSingleTapUpEvent(e))
        return true
      }

      override fun onDown(e: MotionEvent): Boolean {
        onGestureEvent(GestureEvent.createDownEvent(e))
        return true
      }
    })
    binding.surfaceView.setOnTouchListener { _, event ->
      detector.onTouchEvent(event)
    }
  }

  override fun onResume() {
    super.onResume()

    arSession = ARSession(this)
    val config = ARWorldTrackingConfig(arSession).apply {
      focusMode = ARConfigBase.FocusMode.AUTO_FOCUS
      semanticMode = ARWorldTrackingConfig.SEMANTIC_PLANE
    }

    arSession?.configure(config)
    worldRenderManager.setArSession(arSession)
    arSession?.resume()

    rotationManager.registerDisplayListener()
    binding.surfaceView.onResume()
  }

  override fun onPause() {
    super.onPause()
    arSession?.let {
      rotationManager.unregisterDisplayListener()
      binding.surfaceView.onPause()
      it.pause()
    }
  }

  override fun onDestroy() {
    arSession?.stop()
    arSession = null
    super.onDestroy()
  }

  fun provideViewList(): List<ImageView> = listOf(binding.dickbutt, binding.dickbutt02, binding.dickbutt03)

  private fun onGestureEvent(e: GestureEvent) {
    val offerResult: Boolean = tapQueue.offer(e)
    if (offerResult) {
      Timber.d("Successfully joined the queue.")
    } else {
      Timber.d("Failed to join queue.")
    }
  }
}