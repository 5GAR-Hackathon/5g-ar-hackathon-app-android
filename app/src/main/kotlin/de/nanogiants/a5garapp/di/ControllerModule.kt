package de.nanogiants.a5garapp.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.LocationController
import de.nanogiants.a5garapp.controllers.LocationControllerImpl
import de.nanogiants.a5garapp.controllers.PermissionController
import de.nanogiants.a5garapp.controllers.PermissionControllerImpl
import de.nanogiants.a5garapp.controllers.SharedPreferencesController
import de.nanogiants.a5garapp.controllers.SharedPreferencesControllerImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module(includes = [BaseActivityModule::class])
@InstallIn(ActivityComponent::class)
abstract class ControllerModule {

  @Binds
  abstract fun providePermissionController(permissionControllerImpl: PermissionControllerImpl): PermissionController

  @Binds
  abstract fun provideLocationController(locationControllerImpl: LocationControllerImpl): LocationController

  @Binds
  abstract fun provideSharedPreferencesController(sharedPreferencesControllerImpl: SharedPreferencesControllerImpl): SharedPreferencesController
}

@Module
@InstallIn(ActivityComponent::class)
object BaseActivityModule {

  @Provides
  fun provideBaseActivity(activity: Activity): BaseActivity {
    check(activity is BaseActivity) { "Every Activity is expected to extend BaseActivity" }
    return activity
  }

  @Provides
  fun provideSharedPreferences(activity: Activity): SharedPreferences =
    activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
}