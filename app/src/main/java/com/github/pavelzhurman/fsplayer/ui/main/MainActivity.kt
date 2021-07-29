package com.github.pavelzhurman.fsplayer.ui.main


import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.core.Stubs
import com.github.pavelzhurman.core.base.BaseActivity
import com.github.pavelzhurman.freesound_api.datasource.FreesoundRepositoryImpl
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.ActivityMainBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity
import com.github.pavelzhurman.image_loader.ImageLoader
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private var drawerLayout: DrawerLayout? = null

    override fun initObservers(viewModel: MainViewModel) {}

    override fun initViews() {
        initToolbar()
        initDrawerLayout()
        initNavigation()
        initMiniPlayerView()
    }


    private fun initMiniPlayerView() {
        val url = Stubs.Images().FAKE_POSTER_NYAN_CAT

        val miniPlayerBinding = binding.appBar.contentMain.miniPlayerView.getBinding()

        miniPlayerBinding?.apply {
            root.setOnClickListener { PlayerActivity.start(this@MainActivity) }
            textViewArtist.text = Stubs.Texts().ARTIST_EMINEM
            textViewSongName.text = Stubs.Texts().SONG_LOSE_YOURSELF
            seekBar.progress = 100
            ImageLoader().loadPoster(this@MainActivity, url, imageAlbum)
        }
    }


    private fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initNavigation() {
        val navigationView = binding.navigationView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navigationView.setupWithNavController(navController)
    }

    private fun initDrawerLayout() {
        drawerLayout = binding.drawerLayout

        val actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open,
                R.string.close
            )
        drawerLayout?.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout?.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

}