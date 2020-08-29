package de.nanogiants.a5garapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.nanogiants.a5garapp.utils.DebugTree
import timber.log.Timber

@HiltAndroidApp
class HackathonApp : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) Timber.plant(DebugTree())
  }
}