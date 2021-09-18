package com.github.pavelzhurman.fsplayer.ui.freesound.freesoundItem

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModelProvider
import com.github.pavelzhurman.core.ProjectConstants.NOT_DOWNLOADED_SONG_ID
import com.github.pavelzhurman.core.TimeConverters.convertDurationFromDoubleSecToIntMillis
import com.github.pavelzhurman.core.TimeConverters.convertFromMillisToMinutesAndSeconds
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.fsplayer.App
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreesoundItemBinding
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.fsplayer.ui.freesound.FreeSoundSearchViewModel
import com.github.pavelzhurman.fsplayer.ui.freesound.FreesoundSearchViewModelFactory
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import javax.inject.Inject

class FreesoundItemFragment : BaseFragment<FragmentFreesoundItemBinding>() {

    @Inject
    lateinit var freesoundViewModelFactory: FreesoundSearchViewModelFactory<FreeSoundSearchViewModel>
    lateinit var mainComponent: MainComponent

    private var audioPlayerService: AudioPlayerService? = null

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

    override fun initViews() {
        mainComponent = App().provideMainComponent()
        mainComponent.inject(this)
        bindToAudioService()

        viewModel.freesoundSongItemLiveData.observe(this, { item ->
            with(binding) {
                textViewSongName.text = item.name
                textViewDescription.text = getString(R.string.description, item.description)
                val fileSizeKbs: Int = item.filesize / 1000
                textViewFileSize.text = getString(R.string.file_size, fileSizeKbs)
                textViewTags.text = getString(
                    R.string.tags,
                    item.tags.joinToString(separator = ", ", prefix = "[", postfix = "]")
                )
                textViewAuthorName.text = getString(R.string.username, item.username)

                val listOfImages = listOf(
                    item.images.spectral_bw_l,
                    item.images.spectral_l,
                    item.images.waveform_l,
                    item.images.waveform_bw_l,
                )

                recyclerViewImages.adapter = FreesoundItemAdapter(listOfImages)
                textViewDuration.text = getString(
                    R.string.duration,
                    convertFromMillisToMinutesAndSeconds(
                        convertDurationFromDoubleSecToIntMillis(item.duration).toLong()
                    )
                )
                textViewNumDownloads.text = getString(R.string.num_downloads, item.num_downloads)
                imageButtonPlay.setOnClickListener {
                    val songItem = SongItem(
                        songId = NOT_DOWNLOADED_SONG_ID,
                        uri = item.previews.preview_hq_mp3,
                        name = item.name,
                        title = item.description,
                        artist = item.name,
                        duration = convertDurationFromDoubleSecToIntMillis(item.duration),
                        albumUri = item.images.waveform_m,
                    )
                    audioPlayerService?.setSource(songItem)
                }
                imageButtonDownload.setOnClickListener {
                    viewModel.downloadManagerForFreesoundSongItems.downloadFreesoundSongItem(
                        fileName = item.name,
                        url = item.previews.preview_hq_mp3,
                        notificationTitle = item.name
                    )
                }
            }
        })

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

    override fun getViewBinding(): FragmentFreesoundItemBinding =
        FragmentFreesoundItemBinding.inflate(layoutInflater)
}