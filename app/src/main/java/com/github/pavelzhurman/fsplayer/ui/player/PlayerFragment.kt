package com.github.pavelzhurman.fsplayer.ui.player

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.exoplayer.CURRENT_POSITION_TAG
import com.github.pavelzhurman.exoplayer.PlayerStatus
import com.github.pavelzhurman.exoplayer.SEND_CURRENT_POSITION_ACTION
import com.github.pavelzhurman.fsplayer.App
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlayerBinding
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.fsplayer.ui.player.viewpager.PlayerPagerAdapter
import com.github.pavelzhurman.fsplayer.ui.player.viewpager.ZoomOutPageTransformer
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import com.google.android.material.snackbar.Snackbar

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    private val zoomOutPageTransformer = ZoomOutPageTransformer()

    private var audioPlayerService: AudioPlayerService? = null
    private var broadcastReceiverForData: BroadcastReceiver? = null
    private val playerStatusMutableLiveData = MutableLiveData<PlayerStatus>()

    private var localIndex = 0

    private val drawableImagePause by lazy {
        AppCompatResources.getDrawable(
            requireContext(),
            com.github.pavelzhurman.ui.R.drawable.ic_baseline_pause_24
        )
    }
    private val drawableImagePlay by lazy {
        AppCompatResources.getDrawable(
            requireContext(),
            com.github.pavelzhurman.ui.R.drawable.ic_baseline_play_arrow_24
        )
    }

    private val colorEnabled by lazy {
        ContextCompat.getColor(requireContext(), R.color.small_icons_color_enabled)
    }
    private val colorNotEnabled by lazy {
        ContextCompat.getColor(requireContext(), R.color.small_icons_color)
    }

    private var listOfSongs: List<SongItem> = emptyList()

    lateinit var mainComponent: MainComponent

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service

            initViews()

            audioPlayerService?.playerStatusLiveData?.observe(
                this@PlayerFragment,
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

    override fun initViews() {
        if (audioPlayerService == null) {
            bindToAudioService()
        } else {

            initBroadcastReceiver()
            initObservers()

            initButtonShuffleSOCL()
            initSeekBarSOCL()
            initButtonRepeatSOCL()
            with(binding) {
                imageButtonPlay.setOnClickListener {
                    if (audioPlayerService?.isPlayerPlaying() == true) {
                        imageButtonPauseSOCL()
                    } else {
                        imageButtonPlaySOCL()
                    }
                }
                imageButtonNext.setOnClickListener {
                    audioPlayerService?.next()
                }
                imageButtonPrevious.setOnClickListener {
                    audioPlayerService?.goToPreviousOrBeginning()
                }

            }
        }
    }

    private fun initButtonRepeatSOCL() {
        binding.imageButtonRepeat.apply {
            setOnClickListener {
                when (audioPlayerService?.manageRepeatMode()) {
                    1 -> {
                        drawable.setTint(colorEnabled)
                        Snackbar.make(it, R.string.repeat_mode_one, Snackbar.LENGTH_SHORT).show()
                    }
                    2 -> {
                        drawable.setTint(colorNotEnabled)
                        Snackbar.make(it, R.string.repeat_mode_all, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun initButtonShuffleSOCL() {
        binding.imageButtonShuffle.apply {
            setOnClickListener {
                val turnedOn = audioPlayerService?.manageShuffleMode()
                if (turnedOn == true) {
                    drawable.setTint(colorEnabled)
                    Snackbar.make(it, R.string.shuffle_mode_on, Snackbar.LENGTH_SHORT).show()
                } else {
                    drawable.setTint(colorNotEnabled)
                    Snackbar.make(it, R.string.shuffle_mode_off, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun imageButtonPauseSOCL() {
        changeDrawableToPlay()
        audioPlayerService?.pause()
    }

    private fun imageButtonPlaySOCL() {
        changeDrawableToPause()

        if (audioPlayerService == null) {
            bindToAudioService()
            audioPlayerService?.resume()

        } else {
            audioPlayerService?.resume()
        }
    }

    private fun initSeekBarSOCL() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    private fun changeDrawableToPause() {
        binding.apply {
            ImageLoader().loadDrawable(imageButtonPlay, drawableImagePause, imageButtonPlay)
        }
    }

    private fun changeDrawableToPlay() {
        binding.apply {
            ImageLoader().loadDrawable(imageButtonPlay, drawableImagePlay, imageButtonPlay)
        }
    }

    private fun initBroadcastReceiver() {
        val filterForCurrentPosition = IntentFilter(SEND_CURRENT_POSITION_ACTION)

        broadcastReceiverForData = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (context != null) {
                    when (intent?.action) {
                        SEND_CURRENT_POSITION_ACTION -> {
                            val currentPosition = intent.getLongExtra(CURRENT_POSITION_TAG, 0)
                            binding.seekBar.progress = convertFromMillisToPercents(currentPosition)
                            binding.textViewCurrentPosition.text =
                                convertFromMillisToMinutesAndSeconds(currentPosition)
                            binding.textViewDuration.text =
                                audioPlayerService?.getCurrentMediaItemDuration()?.let {
                                    convertFromMillisToMinutesAndSeconds(it)
                                }
                        }
                    }
                }

            }
        }
        activity?.registerReceiver(broadcastReceiverForData, filterForCurrentPosition)
    }

    private fun convertFromMillisToMinutesAndSeconds(millis: Long): String {
        return if (millis > 0L) {
            val hours: Long = millis / 3600000L
            val minutes: Long = (millis - (hours * 3600000L)) / 60000L
            val seconds: Long = (millis - (minutes * 60000L)) / 1000L
            if (hours <= 0L) {
                if (seconds < 10L) "$minutes:0$seconds"
                else "$minutes:$seconds"
            } else {
                if (minutes < 10) "$hours:0$minutes:$seconds"
                else "$hours:$minutes:$seconds"
            }
        } else context?.getString(R.string.zero_time) ?: "0:00"
    }

    private fun convertFromMillisToPercents(currentPosition: Long): Int {
        return if (currentPosition != 0L) {
            val duration = audioPlayerService?.getCurrentMediaItemDuration() ?: 0
            if (duration != 0L) {
                val result: Double =
                    (currentPosition.toDouble() / duration.toDouble()) * 100.0

                result.toInt()
            } else 0
        } else 0
    }

    private fun bindToAudioService() {
        if (audioPlayerService == null) {
            activity?.bindService(
                Intent(activity, AudioPlayerService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun unbindAudioService() {
        if (audioPlayerService != null) {
            activity?.unbindService(connection)

            audioPlayerService = null
        }
    }

    override fun onStart() {
        super.onStart()
        bindToAudioService()
    }

    override fun onStop() {
        super.onStop()
        unbindAudioService()
    }

    private fun stopAudioService() {
        audioPlayerService?.pause()

        unbindAudioService()
        activity?.stopService(Intent(activity, AudioPlayerService::class.java))

        audioPlayerService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mainComponent = App().provideMainComponent()
        mainComponent.inject(this)
    }

    private fun initObservers() {
        audioPlayerService?.listOfSongsLiveData?.observe(viewLifecycleOwner, { resultList ->
            if (resultList.isNotEmpty()) {
                listOfSongs = resultList
                localIndex = audioPlayerService?.getCurrentWindowIndex() ?: 0
                initViewPager(listOfSongs, localIndex)
            }

        })
        audioPlayerService?.currentIndexLiveData?.observe(viewLifecycleOwner, { index ->
            if (listOfSongs.isNotEmpty()) {
                binding.viewPager.setCurrentItem(index, true)
                binding.textViewDuration.text =
                    convertFromMillisToMinutesAndSeconds(listOfSongs[index].duration.toLong())
                binding.textViewCurrentPosition.text =
                    audioPlayerService?.getCurrentPosition()?.let {
                        convertFromMillisToMinutesAndSeconds(it)
                    }
            }
        })

        playerStatusMutableLiveData.observe(this, { playerStatus ->
            when (playerStatus) {
                is PlayerStatus.Playing -> changeDrawableToPause()
                is PlayerStatus.Paused -> changeDrawableToPlay()
                is PlayerStatus.Cancelled -> Snackbar.make(
                    binding.root,
                    getString(R.string.player_canceled),
                    Snackbar.LENGTH_SHORT
                ).show()
                is PlayerStatus.Error -> Snackbar.make(
                    binding.root,
                    getString(R.string.player_error),
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

    private fun initViewPager(listOfSongs: List<SongItem>, index: Int) {
        with(binding.viewPager) {

            // Set offscreen page limit to at least 1, so adjacent pages are always laid out
            offscreenPageLimit = 1

            val recyclerView = getChildAt(0) as RecyclerView

            recyclerView.apply {
                val padding1 = resources.getDimensionPixelOffset(R.dimen.halfPageMargin)
                val padding2 = resources.getDimensionPixelOffset(R.dimen.peekOffset)

                val padding = padding1 + padding2
                // setting padding on inner RecyclerView puts overscroll effect in the right place
                setPadding(padding, 0, padding, 0)

                clipToPadding = false

            }
            this.adapter = PlayerPagerAdapter(listOfSongs)
            this.setCurrentItem(index, false)
            this.setPageTransformer(zoomOutPageTransformer)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    audioPlayerService?.setCurrentWindowIndex(position)
                }
            })
        }
    }

    override fun getViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)
}