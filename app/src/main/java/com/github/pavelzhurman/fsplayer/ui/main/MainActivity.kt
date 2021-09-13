package com.github.pavelzhurman.fsplayer.ui.main


import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.pavelzhurman.core.Stubs
import com.github.pavelzhurman.core.base.BaseActivity
import com.github.pavelzhurman.exoplayer.ALBUM_URI_TAG
import com.github.pavelzhurman.exoplayer.ARTIST_TAG
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.exoplayer.CURRENT_POSITION_TAG
import com.github.pavelzhurman.exoplayer.DURATION_TAG
import com.github.pavelzhurman.exoplayer.PlayerStatus
import com.github.pavelzhurman.exoplayer.SEND_CURRENT_POSITION_ACTION
import com.github.pavelzhurman.exoplayer.SEND_DATA_TO_MINI_PLAYER_ACTION
import com.github.pavelzhurman.exoplayer.TITLE_TAG
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.ActivityMainBinding
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.fsplayer.di.main.MainComponentProvider
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity
import com.github.pavelzhurman.image_loader.ImageLoader
import com.google.android.material.snackbar.Snackbar

private const val MY_READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1001

class MainActivity : BaseActivity<ActivityMainBinding>() {

    lateinit var mainComponent: MainComponent

    private val miniPlayerView by lazy { binding.appBar.contentMain.miniPlayerView }
    private val miniPlayerBinding by lazy { binding.appBar.contentMain.miniPlayerView.getBinding() }

    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)
            .get(MainViewModel::class.java)
    }
    private var drawerLayout: DrawerLayout? = null

    private val playerStatusMutableLiveData = MutableLiveData<PlayerStatus>()

    private var audioPlayerService: AudioPlayerService? = null

    private var duration = 0

    private var broadcastReceiverForData: BroadcastReceiver? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service

            audioPlayerService?.playerStatusLiveData?.observe(
                this@MainActivity,
                { playerStatus ->
                    playerStatusMutableLiveData.value = playerStatus
                    if (playerStatus is PlayerStatus.Cancelled) {
                        stopAudioService()
                    }
                })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }
    }

    private fun bindToAudioService() {
        if (audioPlayerService == null) {
            bindService(
                Intent(this, AudioPlayerService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun stopAudioService() {
        audioPlayerService?.pause()
        unbindAudioService()
        stopService(Intent(this, AudioPlayerService::class.java))
        audioPlayerService = null
    }

    private fun unbindAudioService() {
        if (audioPlayerService != null) {
            unbindService(connection)
            audioPlayerService = null
        }
    }

    override fun onStart() {
        super.onStart()
        bindToAudioService()

        when (playerStatusMutableLiveData.value) {
            PlayerStatus.Playing() -> miniPlayerView.changeDrawableToPause()
            else -> {
                miniPlayerView.changeDrawableToPlay()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (audioPlayerService?.isPlayerPlaying() == true) {
            miniPlayerView.changeDrawableToPause()
        } else {
            miniPlayerView.changeDrawableToPlay()
        }
    }

    override fun onStop() {
        super.onStop()
        unbindAudioService()
    }

    override fun initViews() {
        initApp()
        initAudioService()
        askPermissionsAndCollectAudioFromExternalStorage()
        initBroadcastReceiver()
        initToolbar()
        initDrawerLayout()
        initNavigation()
        initObservers()
        setOnclickListenersOnMiniPlayerView()
    }

    private fun initApp() {
        mainComponent = (applicationContext as MainComponentProvider).provideMainComponent()
        mainComponent.inject(this)
    }

    private fun collectAudioFromExternalStorage() {
        viewModel.collectAudioAndAddToMainPlaylist()
//        musicDatabaseRepositoryImpl?.initFavouritePlaylist()
    }

    private fun initAudioService() {
        startService(Intent(this, AudioPlayerService::class.java))
        bindToAudioService()
    }

    private fun initBroadcastReceiver() {
        val filterForData = IntentFilter(SEND_DATA_TO_MINI_PLAYER_ACTION)
        val filterForCurrentPosition = IntentFilter(SEND_CURRENT_POSITION_ACTION)


        broadcastReceiverForData = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                when (intent?.action) {
                    SEND_DATA_TO_MINI_PLAYER_ACTION -> {
                        val artist = intent.getStringExtra(ARTIST_TAG)
                        val title = intent.getStringExtra(TITLE_TAG)
                        val uriImage = intent.getStringExtra(ALBUM_URI_TAG)
                            ?: Stubs.Images().FAKE_POSTER_NYAN_CAT
                        duration = intent.getIntExtra(DURATION_TAG, 0)

                        miniPlayerBinding?.apply {
                            textViewArtist.text = artist
                            textViewSongName.text = title

                            miniPlayerBinding?.imageAlbum?.let { imageView ->
                                if (context != null) {
                                    ImageLoader().loadPoster(
                                        context, uriImage,
                                        imageView
                                    )
                                }
                            }
                        }
                    }
                    SEND_CURRENT_POSITION_ACTION -> {
                        val currentPosition =
                            intent.getLongExtra(CURRENT_POSITION_TAG, 0)
                        miniPlayerBinding?.seekBar?.progress =
                            convertFromMillisToPercents(currentPosition)
                    }
                }
            }
        }
        registerReceiver(broadcastReceiverForData, filterForCurrentPosition)
        registerReceiver(broadcastReceiverForData, filterForData)
    }

    private fun convertFromMillisToPercents(currentPosition: Long): Int {
        return if (currentPosition != 0L) {
            if (duration != 0) {
                val result: Double =
                    (currentPosition.toDouble() / duration.toDouble()) * 100.0
                result.toInt()
            } else 0
        } else 0
    }

    private fun initObservers() {
        playerStatusMutableLiveData.observe(this, { playerStatus ->

            when (playerStatus) {
                is PlayerStatus.Playing -> miniPlayerView.changeDrawableToPause()
                is PlayerStatus.Paused -> miniPlayerView.changeDrawableToPlay()
                is PlayerStatus.Cancelled -> Snackbar.make(
                    miniPlayerView,
                    "Player canceled",
                    Snackbar.LENGTH_SHORT
                ).show()
                is PlayerStatus.Error -> Snackbar.make(
                    miniPlayerView,
                    "Player error",
                    Snackbar.LENGTH_SHORT
                ).show()
                is PlayerStatus.Buffering -> {
//                    TODO
                }
                else -> {
                }
            }

        })
    }

    private fun imageButtonPlaySOCL() {
        miniPlayerView.changeDrawableToPause()

        if (audioPlayerService == null) {
            bindToAudioService()
            audioPlayerService?.resume()

        } else {
            audioPlayerService?.resume()
        }
    }

    private fun imageButtonPauseSOCL() {
        miniPlayerView.changeDrawableToPlay()
        audioPlayerService?.pause()
    }

    private fun initSeekBarSOCL(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { audioPlayerService?.seekTo(it) }
            }
        })
    }

    private fun setOnclickListenersOnMiniPlayerView() {
        miniPlayerBinding?.apply {
            root.setOnClickListener { PlayerActivity.start(this@MainActivity) }
            imageButtonPlay.setOnClickListener {
                if (audioPlayerService?.isPlayerPlaying() == true) {
                    imageButtonPauseSOCL()
                } else {
                    imageButtonPlaySOCL()
                }
            }
            imageButtonNext.setOnClickListener { audioPlayerService?.next() }
            imageButtonPrevious.setOnClickListener { audioPlayerService?.goToPreviousOrBeginning() }
            initSeekBarSOCL(seekBar)
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


    private fun askPermissionsAndCollectAudioFromExternalStorage() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("TAGTAGTAG", "PERMISSION denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            }

        } else {
            Log.v("TAGTAGTAG", "PERMISSION accessed")
            collectAudioFromExternalStorage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_READ_EXTERNAL_STORAGE_PERMISSION_CODE && PackageManager.PERMISSION_GRANTED == grantResults[0]
        ) {
            collectAudioFromExternalStorage()
        }
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

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
}