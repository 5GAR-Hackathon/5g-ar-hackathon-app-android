package de.nanogiants.a5garapp.activities

import android.opengl.GLSurfaceView
import com.huawei.hiar.ARConfigBase
import com.huawei.hiar.ARSession
import com.huawei.hiar.ARWorldTrackingConfig
import de.nanogiants.a5garapp.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityArTestBinding
import de.nanogiants.a5garapp.hms.common.DisplayRotationManager
import de.nanogiants.a5garapp.hms.rendering.WorldRenderManager

class ARTestActivity : BaseActivity() {

  private val rotationManager by lazy {
    DisplayRotationManager(this)
  }
  private val worldRenderManager by lazy {
    WorldRenderManager(this)
  }

  private var arSession: ARSession? = null

  override val binding: ActivityArTestBinding by viewBinding(ActivityArTestBinding::inflate)

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
}