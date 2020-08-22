package de.nanogiants.a5garapp.controllers

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import de.nanogiants.a5garapp.base.BaseActivity
import javax.inject.Inject

class PermissionControllerImpl @Inject constructor() : PermissionController {

  @Inject
  lateinit var activity: BaseActivity

  override fun hasPermissions(vararg permissions: String): Boolean {
    return permissions.all { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED }
  }

  override fun requestPermissions(vararg permissions: String, onGranted: (() -> Unit), onDenied: () -> Unit) {
    activity.registerForActivityResult(RequestMultiplePermissions()) { result ->
      if (result.all { it.value }) onGranted() else onDenied()
    }.launch(permissions)
  }
}