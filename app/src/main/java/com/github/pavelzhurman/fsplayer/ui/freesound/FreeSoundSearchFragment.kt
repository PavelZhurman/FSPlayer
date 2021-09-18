package com.github.pavelzhurman.fsplayer.ui.freesound

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.core.ProjectConstants
import com.github.pavelzhurman.core.ProjectConstants.NOT_DOWNLOADED_SONG_ID
import com.github.pavelzhurman.core.TimeConverters.convertDurationFromDoubleSecToIntMillis
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundSearchBinding
import com.github.pavelzhurman.fsplayer.App
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class FreeSoundSearchFragment :
    BaseFragment<FragmentFreeSoundSearchBinding>() {

    @Inject
    lateinit var freesoundViewModelFactory: FreesoundSearchViewModelFactory<FreeSoundSearchViewModel>

    lateinit var mainComponent: MainComponent

    private var audioPlayerService: AudioPlayerService? = null
    private var nextPageNumber: Int? = null
    private var query: String? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }

    }

    val viewModel: FreeSoundSearchViewModel by lazy {
        ViewModelProvider(this, freesoundViewModelFactory)
            .get(FreeSoundSearchViewModel::class.java)
    }

    private var freesoundSearchAdapter: FreesoundSearchDataItemAdapter =
        FreesoundSearchDataItemAdapter()

    private fun initObservers() {
        viewModel.countLiveData.observe(this, { count ->
            binding.textViewCount.text = getString(R.string.sounds_found_with_value, count)
        })

        viewModel.nextLiveData.observe(this, { next ->
            if (next == null) {
                Logger().logcatD("NextPageCheckTag", next.toString())
                nextPageNumber = null
            } else {
                Logger().logcatD("NextPageCheckTag", next.toString())
                nextPageNumber = next
            }
        })

        viewModel.apply {
            freesoundSongItemListLiveData.observe(
                viewLifecycleOwner,
                { data -> setValuesInAdapter(data) }
            )
        }

        viewModel.freesoundSongItemListNextPagesLiveData.observe(this, { item ->
            addValueToList(item)
        })

    }

    private fun setValuesInAdapter(list: List<FreesoundSongItem>) {
        if (list.isNullOrEmpty()) binding.textViewCount.text = getString(R.string.sounds_found_zero)
        freesoundSearchAdapter.values = (list as MutableList<FreesoundSongItem>)
    }

    private fun addValueToList(songItem: FreesoundSongItem) {
        freesoundSearchAdapter.values.add(songItem)
        freesoundSearchAdapter.notifyDataSetChanged()
    }

    override fun initViews() {
        mainComponent = App().provideMainComponent()
        mainComponent.inject(this)
        initObservers()

        bindToAudioService()
        with(binding) {

            recyclerView.apply {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1)) {
                            nextPageNumber?.let { page ->
                                query?.let { query ->
                                    viewModel.fetchFreesoundSearchData(
                                        query, page
                                    )
                                }
                            }
                        }
                    }

                })
                adapter = freesoundSearchAdapter.apply {
                    onPlayButtonClickListener = { freesoundSongItem ->
                        val songItem = SongItem(
                            songId = NOT_DOWNLOADED_SONG_ID,
                            uri = freesoundSongItem.previews.preview_hq_mp3,
                            name = freesoundSongItem.name,
                            title = freesoundSongItem.description,
                            artist = freesoundSongItem.name,
                            duration = convertDurationFromDoubleSecToIntMillis(freesoundSongItem.duration),
                            albumUri = freesoundSongItem.images.waveform_m,
                        )
                        audioPlayerService?.setSource(songItem)
                    }
                    onDownloadClickListener = { item ->
                        viewModel.downloadSong(item)

                        val onComplete = object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, intent: Intent?) {
                                when (intent?.action) {
                                    ProjectConstants.DOWNLOAD_STATUS_ACTION -> {

                                        intent.extras.let { bundle ->

                                            val status =
                                                bundle?.getInt(ProjectConstants.DOWNLOAD_STATUS_TAG)
                                            val reason =
                                                bundle?.getString(ProjectConstants.DOWNLOAD_STATUS_REASON)
                                            val progress =
                                                bundle?.getFloat(ProjectConstants.DOWNLOAD_STATUS_PROGRESS)

                                            Logger().logcatD(
                                                "DownloadStatusTAGTAG",
                                                "status $status"
                                            )
                                            Logger().logcatD(
                                                "DownloadStatusTAGTAG",
                                                "reason $reason"
                                            )
                                            Logger().logcatD(
                                                "DownloadStatusTAGTAG",
                                                "progress $progress"
                                            )


                                        }
                                    }
                                    DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                                        Logger().logcatD(
                                            "DownloadStatusTAGTAG",
                                            "${item.name} downloaded"
                                        )

                                        activity?.getString(R.string.downloaded, item.name)?.let {
                                            Snackbar.make(
                                                binding.recyclerView,
                                                it,
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                            }

                        }
                        context?.registerReceiver(
                            onComplete,
                            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        )
                    }
                    onItemClickListener = { item ->
                        openFreesoundItemFragment(item)
                    }
                }
                layoutManager = LinearLayoutManager(context)
            }

        }
        initButtonSearch()
    }

    private fun openFreesoundItemFragment(item: FreesoundSongItem) {
        viewModel.setFreesoundSongItem(item)
        Navigation.findNavController(requireView())
            .navigate(R.id.action_freeSoundSearchFragment_to_freesoundItemFragment)
    }

    private fun initButtonSearch() {
        with(binding) {
            buttonSearch.setOnClickListener {
                query = editTextSearchBy.text.toString()
                if (query != null) {
                    viewModel.fetchFreesoundSearchData(query!!)
                    freesoundSearchAdapter.values.clear()
                }
            }
        }
    }

    private fun bindToAudioService() {
        if (audioPlayerService == null) {
            activity?.bindService(
                Intent(this.context, AudioPlayerService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindAudioService()
    }

    private fun unbindAudioService() {
        if (audioPlayerService != null) {
            context?.unbindService(connection)
            audioPlayerService = null
        }
    }

    override fun getViewBinding() =
        FragmentFreeSoundSearchBinding.inflate(layoutInflater)
}