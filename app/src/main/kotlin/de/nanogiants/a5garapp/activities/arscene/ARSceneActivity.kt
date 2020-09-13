package de.nanogiants.a5garapp.activities.arscene

import android.view.View
import android.view.View.*
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityArSceneBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ARSceneActivity : BaseActivity() {

  override val binding: ActivityArSceneBinding by viewBinding(ActivityArSceneBinding::inflate)

  var modelName = ""

  var modelLoaded = false

  lateinit var loadModelJob: Job

  override fun initView() {
    val model = intent.getSerializableExtra("model") as String
    val poi = intent.getSerializableExtra("poi") as POI

    modelName = "ARModels/${model}/model.gltf"

    binding.arView.enablePlaneDisplay(false)
    loadModelJob = tryLoadModel()

    binding.poiNameTextView.text = poi.name
    binding.poiDescriptionTextView.text = poi.description
    binding.descriptionContainer.visibility = if (poi.description.isBlank()) GONE else VISIBLE
    binding.closeIcon.setOnClickListener { binding.descriptionContainer.visibility = GONE }
  }

  override fun onPause() {
    super.onPause()
    binding.arView.onPause()
    loadModelJob.cancel()
  }

  override fun onResume() {
    super.onResume()
    binding.arView.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.arView.destroy()
  }

  private fun tryLoadModel(): Job {
    return lifecycleScope.launch {
      while (!modelLoaded) {
        val result = binding.arView.loadAsset(modelName)
        binding.arView.setInitialPose(BASE_MODEL_SCALE, BASE_MODEL_ROTATION)

        if (result != -1) {
          modelLoaded = true
          loadModelJob.cancel()
        }

        delay(1000)
      }
    }
  }

  companion object {
    val BASE_MODEL_SCALE_FACTOR = 0.0025f
    val BASE_MODEL_ROTATION_FACTOR = 0f

    val BASE_MODEL_SCALE =
      floatArrayOf(BASE_MODEL_SCALE_FACTOR, BASE_MODEL_SCALE_FACTOR, BASE_MODEL_SCALE_FACTOR)
    val BASE_MODEL_ROTATION = floatArrayOf(
      BASE_MODEL_ROTATION_FACTOR,
      BASE_MODEL_ROTATION_FACTOR,
      BASE_MODEL_ROTATION_FACTOR,
      BASE_MODEL_ROTATION_FACTOR
    )
  }
}