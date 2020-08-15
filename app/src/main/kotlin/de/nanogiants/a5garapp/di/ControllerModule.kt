package de.nanogiants.a5garapp.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import de.nanogiants.a5garapp.BaseActivity
import de.nanogiants.a5garapp.controllers.LocationController
import de.nanogiants.a5garapp.controllers.LocationControllerImpl
import de.nanogiants.a5garapp.controllers.PermissionController
import de.nanogiants.a5garapp.controllers.PermissionControllerImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(ActivityComponent::class)
object ControllerModule {

  @Provides
  fun providePermissionController(activity: Activity): PermissionController {
    return PermissionControllerImpl(activity as BaseActivity)
  }

  @Provides
  fun provideLocationController(activity: Activity): LocationController {
    return LocationControllerImpl(activity as BaseActivity)
  }
}