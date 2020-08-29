package de.nanogiants.a5garapp.activities.dashboard

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardPOIAdapter
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.model.datastore.POIDatastore
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

  @Inject
  lateinit var poiDatastore: POIDatastore

  // @Inject
  // lateinit var tagDatastore: TagDatastore

  override fun initView() {
    poiAdapter = DashboardPOIAdapter()
    poiLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    binding.dashboardPoiRecyclerview.apply {
      layoutManager = poiLayoutManager
      adapter = poiAdapter
    }
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
}