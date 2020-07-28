package de.nanogiants.a5garapp

import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import de.nanogiants.a5garapp.databinding.ActivityMainBinding

// TODO move hilt annotation to BaseActivity when https://github.com/google/dagger/issues/1955 is shipped
@AndroidEntryPoint
class MainActivity : BaseActivity() {

  override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

  override fun initView() {
    binding.toolbar.title = resources.getString(R.string.app_name)
  }

  override fun onResume() {
    super.onResume()
    Toast.makeText(this, "Hi Developer!", Toast.LENGTH_SHORT).show()
  }
}