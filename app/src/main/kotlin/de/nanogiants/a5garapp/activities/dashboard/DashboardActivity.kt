package de.nanogiants.a5garapp.activities.dashboard

import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.dashboard.adapters.DashboardPOIAdapter
import de.nanogiants.a5garapp.activities.favorites.FavoritesActivity
import de.nanogiants.a5garapp.activities.poidetail.POIDetailActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityDashboardBinding
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.entities.domain.POI
import de.nanogiants.a5garapp.utils.JSONReader
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

  val loadFromWeb: Boolean = false

  override fun initView() {
    binding.toolbar.title = "Places"

    poiLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    poiAdapter = DashboardPOIAdapter()
    poiAdapter.onPOIClicked = this::onPOIClicked

    binding.dashboardPoiRecyclerview.apply {
      layoutManager = poiLayoutManager
      adapter = poiAdapter
    }

    poiSnapHelper = LinearSnapHelper()
    poiSnapHelper.attachToRecyclerView(binding.dashboardPoiRecyclerview)

    binding.bottomNavigationView.selectedItemId = R.id.places
    binding.bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.favorites -> {
          val intent = Intent(this, FavoritesActivity::class.java)
          // intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

          startActivity(intent)
          overridePendingTransition(0, 0)
          true
        }
        else -> false
      }
    }
  }

  override fun onResume() {
    super.onResume()

    lifecycleScope.launch {
      try {
        if (loadFromWeb) {
          withContext(Dispatchers.IO) { poiDatastore.getAllPOIs() }.let {
            Timber.d("Loaded all $it")
            poiAdapter.clear()
            poiAdapter.addAll(it)
          }
        } else {
          withContext(Dispatchers.IO) { JSONReader.getPOIsFromAssets(this@DashboardActivity) }.let {
            Timber.d("Loaded all $it")
            poiAdapter.clear()
            poiAdapter.addAll(it)
          }
        }
      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
  }

  private fun onPOIClicked(poi: POI, sharedElement: View) {
    Timber.d("Clicked $poi")

    val intent = Intent(this, POIDetailActivity::class.java)
    intent.putExtra("POI", poi)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this@DashboardActivity,
      sharedElement,
      "poiBackgroundImage"
    )

    startActivity(intent, options.toBundle())
  }
}