package de.nanogiants.a5garapp.activities

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.LocationController
import de.nanogiants.a5garapp.controllers.PermissionController
import de.nanogiants.a5garapp.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO move hilt annotation to BaseActivity when https://github.com/google/dagger/issues/1955 is shipped
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity() {

  @Inject
  lateinit var permissionController: PermissionController

  @Inject
  lateinit var locationController: LocationController

  override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

  override fun initView() {
    binding.toolbar.title = resources.getString(R.string.app_name)

    binding.startArTest.setOnClickListener {
      permissionController.requestPermissions(CAMERA, onGranted = {
        startActivity(Intent(this, ARTestActivity::class.java))
      }, onDenied = {})
    }

    binding.startArTest.setOnClickListener {
      permissionController.requestPermissions(ACCESS_FINE_LOCATION, onGranted = {
        startActivity(Intent(this, MapActivity::class.java))
      }, onDenied = {})
    }

    binding.startLocationTest.setOnClickListener {
      permissionController.requestPermissions(ACCESS_FINE_LOCATION, onGranted = {
        binding.startLocationTest.isEnabled = false
        lifecycleScope.launch {
          locationController.getLocation().cancellable().filterNotNull()
            .collect { binding.startLocationTest.text = "Lat ${it.latitude}/Long ${it.longitude}" }
        }
      }, onDenied = {})
    }
  }

  override fun onResume() {
    super.onResume()
    Toast.makeText(this, "Hi Developer!", Toast.LENGTH_SHORT).show()
  }
}