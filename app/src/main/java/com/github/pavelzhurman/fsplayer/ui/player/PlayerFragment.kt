package com.github.pavelzhurman.fsplayer.ui.player

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.pavelzhurman.core.ProjectConstants.CURRENT_POSITION_TAG
import com.github.pavelzhurman.core.ProjectConstants.SEND_CURRENT_POSITION_ACTION
import com.github.pavelzhurman.core.TimeConverters.convertFromMillisToMinutesAndSeconds
import com.github.pavelzhurman.core.TimeConverters.convertFromMillisToPercents
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.exoplayer.PlayerStatus
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
    private val viewModel: PlayerViewModel by activityViewModels()

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
    private var listOfFavouriteSongs: MutableList<SongItem> = mutableListOf()

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
        visibilityOfTextViewNoFound(listOfSongs)
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
                            with(binding) {
                                val duration = audioPlayerService?.getCurrentMediaItemDuration() ?: 0

                                seekBar.progress = convertFromMillisToPercents(currentPosition, duration.toInt())
                                textViewCurrentPosition.text =
                                    convertFromMillisToMinutesAndSeconds(currentPosition)
                                textViewDuration.text =
                                    audioPlayerService?.getCurrentMediaItemDuration()?.let {
                                        convertFromMillisToMinutesAndSeconds(it)
                                    }
                            }

                        }
                    }
                }

            }
        }
        activity?.registerReceiver(broadcastReceiverForData, filterForCurrentPosition)
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
        viewModel.getListOfFavouriteSongs()
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
        initApp()
    }

    private fun initApp() {
        mainComponent = App().provideMainComponent()
        mainComponent.inject(this)
    }

    private fun initObservers() {
        audioPlayerService?.listOfSongsLiveData?.observe(viewLifecycleOwner, { resultList ->
            visibilityOfTextViewNoFound(resultList)
            if (resultList.isNotEmpty()) {
                listOfSongs = resultList
                localIndex = audioPlayerService?.getCurrentWindowIndex() ?: 0

                viewModel.listOfFavouriteSongsLiveData.observe(this, { list ->
                    listOfFavouriteSongs = list

                    initViewPager(listOfSongs, listOfFavouriteSongs, localIndex)

                })
            }

        })
        audioPlayerService?.currentIndexLiveData?.observe(viewLifecycleOwner, { index ->
            if (listOfSongs.isNotEmpty()) {
                with(binding) {
                    viewPager.setCurrentItem(index, true)
                    textViewDuration.text =
                        convertFromMillisToMinutesAndSeconds(listOfSongs[index].duration.toLong())
                    textViewCurrentPosition.text =
                        audioPlayerService?.getCurrentPosition()?.let {
                            convertFromMillisToMinutesAndSeconds(it)
                        }
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
                else -> {
                }
            }

        })


    }

    private fun initViewPager(
        listOfSongs: List<SongItem>,
        listOfFavouriteSongs: MutableList<SongItem>,
        index: Int
    ) {
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
            this.adapter = PlayerPagerAdapter(listOfSongs, listOfFavouriteSongs).apply {
                onDeleteFavouriteSongClickListener = { songItem ->
                    viewModel.removeSongFromFavouritePlaylist(songItem)
                    listOfFavouriteSongs.remove(songItem)
                }
                onAddFavouriteSongClickListener = { songItem ->
                    listOfFavouriteSongs.add(songItem)
                    viewModel.addSongToFavouritePlaylist(songItem)
                }
            }
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

    private fun visibilityOfTextViewNoFound(listOfSongs: List<SongItem>) {
        if (listOfSongs.isNullOrEmpty()) {
            binding.textViewNoSoundsFound.visibility = VISIBLE
        } else {
            binding.textViewNoSoundsFound.visibility = INVISIBLE

        }
    }

    override fun getViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)
}