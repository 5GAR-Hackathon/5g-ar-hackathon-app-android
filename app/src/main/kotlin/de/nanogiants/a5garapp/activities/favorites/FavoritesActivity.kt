package de.nanogiants.a5garapp.activities.favorites

import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.dashboard.DashboardActivity
import de.nanogiants.a5garapp.activities.favorites.adapters.FavoritesAdapter
import de.nanogiants.a5garapp.activities.poidetail.POIDetailActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.controllers.SharedPreferencesController
import de.nanogiants.a5garapp.databinding.ActivityFavoritesBinding
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
class FavoritesActivity : BaseActivity() {

  override val binding: ActivityFavoritesBinding by viewBinding(ActivityFavoritesBinding::inflate)

  lateinit var favoritesAdapter: FavoritesAdapter

  lateinit var favoritesLayoutManager: LayoutManager

  @Inject
  lateinit var sharedPreferencesController: SharedPreferencesController

  override fun initView() {
    binding.toolbar.title = "Favorites"

    favoritesLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    favoritesAdapter = FavoritesAdapter()
    favoritesAdapter.onPOIClicked = this::onPOIClicked

    binding.favoritesRecyclerView.apply {
      layoutManager = favoritesLayoutManager
      adapter = favoritesAdapter
    }

    binding.bottomNavigationView.selectedItemId = R.id.favorites
    binding.bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.places -> {
          val intent = Intent(this, DashboardActivity::class.java)
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
        withContext(Dispatchers.IO) { sharedPreferencesController.getBookmarkedPOIs() }.let {
          Timber.d("Loaded all $it")
          favoritesAdapter.clear()
          favoritesAdapter.addAll(it)
        }

      } catch (e: Exception) {
        Timber.d("There was an error $e")
        Timber.e(e)
      }
    }
  }

  private fun onPOIClicked(poi: POI, sharedElement: View) {
    val intent = Intent(this, POIDetailActivity::class.java)
    intent.putExtra("POI", poi)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      this@FavoritesActivity,
      sharedElement,
      "poiBackgroundImage"
    )

    startActivity(intent, options.toBundle())
  }
}