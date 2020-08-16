package de.nanogiants.a5garapp.di

import android.app.Activity
import dagger.Binds
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
@Module(includes = [BaseActivityModule::class])
@InstallIn(ActivityComponent::class)
abstract class ControllerModule {


  @Binds
  abstract fun providePermissionController(permissionControllerImpl: PermissionControllerImpl): PermissionController

  @Binds
  abstract fun provideLocationController(locationControllerImpl: LocationControllerImpl): LocationController
}

@Module
@InstallIn(ActivityComponent::class)
object BaseActivityModule {

  @Provides
  fun provideBaseActivity(activity: Activity): BaseActivity {
    check(activity is BaseActivity) { "Every Activity is expected to extend BaseActivity" }
    return activity
  }
}