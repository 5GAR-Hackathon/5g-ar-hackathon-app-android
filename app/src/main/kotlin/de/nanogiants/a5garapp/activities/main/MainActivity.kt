package de.nanogiants.a5garapp.activities.main

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.ar.ARTestActivity
import de.nanogiants.a5garapp.activities.dashboard.DashboardActivity
import de.nanogiants.a5garapp.activities.map.MapActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.LocationController
import de.nanogiants.a5garapp.controllers.PermissionController
import de.nanogiants.a5garapp.databinding.ActivityMainBinding
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.datastore.TagDatastore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@ExperimentalCoroutinesApi
@AndroidEntryPoint // TODO move hilt annotation to BaseActivity when https://github.com/google/dagger/issues/1955 is shipped
class MainActivity : BaseActivity() {

  @Inject
  lateinit var permissionController: PermissionController

  @Inject
  lateinit var locationController: LocationController

  @Inject
  lateinit var poiDatastore: POIDatastore

  @Inject
  lateinit var tagDatastore: TagDatastore

  override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

  override fun initView() {
    binding.toolbar.title = resources.getString(R.string.app_name)

    binding.startArTest.setOnClickListener {
      permissionController.requestPermissions(CAMERA, onGranted = {
        startActivity(Intent(this, ARTestActivity::class.java))
      }, onDenied = {})
    }

    binding.startMapTest.setOnClickListener {
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

    binding.startDashboard.setOnClickListener {
      val intent = Intent(this, DashboardActivity::class.java)
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

      startActivity(intent)
    }
  }

  override fun onResume() {
    super.onResume()
    lifecycleScope.launch {
      try {
        withContext(Dispatchers.IO) { poiDatastore.getAllPOIs() }.let {
          binding.backendPois.text = "POIs: ${it.joinToString { poi -> poi.name }}"
        }
      } catch (e: Exception) {
        binding.backendPois.text = "Error loading data"
        Timber.e(e)
      }
      try {
        withContext(Dispatchers.IO) { tagDatastore.getAllTags() }.let {
          binding.backendTags.text = "Tags: ${it.joinToString { tag -> tag.name }}"
        }
      } catch (e: Exception) {
        binding.backendTags.text = "Error loading data"
      }
    }
  }
}