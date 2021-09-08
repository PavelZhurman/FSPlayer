package com.github.pavelzhurman.fsplayer.ui.freesound

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundSearchBinding
import com.github.pavelzhurman.fsplayer.App
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.musicdatabase.downloader.DownloadManagerForFreesoundSongItems
import com.github.pavelzhurman.musicdatabase.downloader.DownloadStatus
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class FreeSoundSearchFragment :
    BaseFragment<FragmentFreeSoundSearchBinding>() {

    @Inject
    lateinit var freesoundViewModelFactory: FreesoundSearchViewModelFactory<FreeSoundSearchViewModel>

    private var downloadManager: DownloadManagerForFreesoundSongItems? = null

    lateinit var mainComponent: MainComponent

    private var audioPlayerService: AudioPlayerService? = null

    private val downloadStatusMutableLiveData: MutableLiveData<DownloadStatus> = MutableLiveData()

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
        viewModel.apply {
            freesoundSongItemListLiveData.observe(
                viewLifecycleOwner,
                { data -> setValuesInAdapter(data) }
            )
        }

        downloadStatusMutableLiveData.observe(this, { status: DownloadStatus ->
            when (status) {
                is DownloadStatus.Downloaded -> {
                    Log.d("DownloadStatusCheckTAG", "Downloaded $status")
                    Snackbar.make(binding.root, "Downloaded", Snackbar.LENGTH_LONG)
                        .show()
                    
                }
                is DownloadStatus.Error -> {
                    Log.d("DownloadStatusCheckTAG", "Error ${status.reason}")
                    Snackbar.make(binding.root, "Error ${status.reason}", Snackbar.LENGTH_LONG)
                        .show()
                }
                is DownloadStatus.Unknown -> {
                    Log.d("DownloadStatusCheckTAG", "Unknown $status")
                    Snackbar.make(
                        binding.root,
                        "Download status unknown",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is DownloadStatus.Downloading -> {
                    Log.d("DownloadStatusCheckTAG", "Downloading ${status.progress}")
                }
            }
        })
    }

    private fun setValuesInAdapter(list: List<FreesoundSongItem>) {
        freesoundSearchAdapter.values = list
    }

    override fun initViews() {
        mainComponent = App().provideMainComponent()
        mainComponent.inject(this)
        initObservers()

        downloadManager = DownloadManagerForFreesoundSongItems(requireContext())
        bindToAudioService()
        with(binding) {
            recyclerView.apply {
                adapter = freesoundSearchAdapter.apply {
                    onPlayButtonClickListener = { freesoundSongItem ->
                        val songItem = SongItem(
                            songId = 0,
                            uri = freesoundSongItem.previews.preview_hq_mp3,
                            name = freesoundSongItem.name,
                            title = freesoundSongItem.description,
                            artist = freesoundSongItem.name,
                            duration = convertDurationFromDoubleSecToIntMillis(freesoundSongItem.duration),
                            albumUri = freesoundSongItem.images.waveform_m,
                            isFavourite = false
                        )
                        audioPlayerService?.setSource(songItem)
                    }
                    onDownloadClickListener = { item ->
                        val downloadId = downloadManager?.downloadFreesoundSongItem(
                            item.name,
                            item.previews.preview_hq_mp3,
                            item.name
                        )
                        if (downloadId != null) {
                            downloadStatusMutableLiveData.value =
                                downloadManager?.getDownloadStatus(downloadId)
                        }
                    }
                }
                layoutManager = LinearLayoutManager(context)
            }

        }
        initButtonSearch()
    }

    private fun convertDurationFromDoubleSecToIntMillis(doubleSec: Double): Int {
        return (doubleSec * 1000).toInt()
    }

    private fun initButtonSearch() {
        with(binding) {
            buttonSearch.setOnClickListener {
                val query: String = editTextSearchBy.text.toString()
                viewModel.fetchFreesoundSearchData(query)
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

    override fun getViewBinding() =
        FragmentFreeSoundSearchBinding.inflate(layoutInflater)
}