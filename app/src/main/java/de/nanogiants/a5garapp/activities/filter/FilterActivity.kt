package de.nanogiants.a5garapp.activities.filter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.R
import de.nanogiants.a5garapp.base.BaseActivity
import de.nanogiants.a5garapp.databinding.ActivityFilterBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FilterActivity : BaseActivity() {

  override val binding: ActivityFilterBinding by viewBinding(ActivityFilterBinding::inflate)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
  }

  override fun initView() {

  }

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.nothing, R.anim.exit_from_top)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        finish()
        overridePendingTransition(R.anim.nothing, R.anim.exit_from_top)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}