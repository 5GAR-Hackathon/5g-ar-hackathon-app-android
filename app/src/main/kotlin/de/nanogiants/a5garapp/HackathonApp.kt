package de.nanogiants.a5garapp

import android.app.Application
import com.huawei.hms.mlsdk.common.MLApplication
import dagger.hilt.android.HiltAndroidApp
import de.nanogiants.a5garapp.utils.DebugTree
import timber.log.Timber

@HiltAndroidApp
class HackathonApp : Application() {

  override fun onCreate() {
    super.onCreate()

    MLApplication.getInstance()
      .setApiKey("CgB6e3x9gfqxB02ElADVb3qDLuH58sBFXY8KZPSgdV6zGn1c7LI/EHbu9fXDxtLTdwpSOHQGzaiv9mcrcJ1K9iaC");

    if (BuildConfig.DEBUG) Timber.plant(DebugTree())
  }
}