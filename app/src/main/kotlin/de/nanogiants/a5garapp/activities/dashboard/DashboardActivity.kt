package de.nanogiants.a5garapp.activities.dashboard

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardPOIAdapter
import de.nanogiants.a5garapp.activities.poidetail.POIDetailActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.entities.domain.POI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

  override val binding: ActivityDashboardBinding by viewBinding(ActivityDashboardBinding::inflate)

  lateinit var poiAdapter: DashboardPOIAdapter

  lateinit var poiLayoutManager: LinearLayoutManager

  lateinit var poiSnapHelper: LinearSnapHelper

  @Inject
  lateinit var poiDatastore: POIDatastore

  // @Inject
  // lateinit var tagDatastore: TagDatastore

  override fun initView() {
    poiLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    poiAdapter = DashboardPOIAdapter()
    poiAdapter.onPOIClicked = this::onPOIClicked

    binding.dashboardPoiRecyclerview.apply {
      layoutManager = poiLayoutManager
      adapter = poiAdapter
    }

    poiSnapHelper = LinearSnapHelper()
    poiSnapHelper.attachToRecyclerView(binding.dashboardPoiRecyclerview)
  }

  override fun onResume() {
    super.onResume()

    lifecycleScope.launch {
      try {
        withContext(Dispatchers.IO) { poiDatastore.getAllPOIs() }.let {
          Timber.d("Loaded all $it")
          poiAdapter.addAll(it)
        }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
      }
    }
  }

  private fun onPOIClicked(poi: POI) {
    Timber.d("Clicked $poi")

    val intent = Intent(this, POIDetailActivity::class.java)
    intent.putExtra("POI", poi)

    startActivity(intent)
  }
}