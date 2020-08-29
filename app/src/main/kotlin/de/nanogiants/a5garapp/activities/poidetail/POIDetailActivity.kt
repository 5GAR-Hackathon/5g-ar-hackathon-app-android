package de.nanogiants.a5garapp.activities.poidetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.databinding.ActivityPoiDetailBinding
import de.nanogiants.a5garapp.model.entities.domain.POI
import timber.log.Timber

class POIDetailActivity : BaseActivity() {

  override val binding: ActivityPoiDetailBinding by viewBinding(ActivityPoiDetailBinding::inflate)

  override fun initView() {
    val poi = intent.getSerializableExtra("POI") as? POI
    Timber.d("New activity $poi")
  }
}