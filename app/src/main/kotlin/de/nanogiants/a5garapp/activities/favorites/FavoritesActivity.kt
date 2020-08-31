package de.nanogiants.a5garapp.activities.favorites

import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.activities.dashboard.DashboardActivity
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityFavoritesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FavoritesActivity : BaseActivity() {

  override val binding: ActivityFavoritesBinding by viewBinding(ActivityFavoritesBinding::inflate)

  override fun initView() {
    binding.bottomNavigationView.selectedItemId = R.id.favorites
    binding.bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.places -> {
          val intent = Intent(this, DashboardActivity::class.java)
          intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

          startActivity(intent)
          overridePendingTransition(0, 0)
          true
        }
        else -> false
      }
    }
  }
}