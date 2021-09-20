package com.github.pavelzhurman.fsplayer.ui.search

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Editable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.fsplayer.databinding.FragmentSearchBinding
import com.github.pavelzhurman.fsplayer.ui.main.MainViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    val viewModel: MainViewModel by activityViewModels()

    private var audioPlayerService: AudioPlayerService? = null
    private var searchFragmentAdapter: SearchFragmentAdapter? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service
            initViews()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }
    }

    override fun initViews() {
        if (audioPlayerService == null) {
            bindToAudioService()
        } else {
            initObservers()
            with(binding) {
                editTextSearchSongs.doAfterTextChanged { text: Editable? ->
                    searchFragmentAdapter?.filter?.filter(text)
                }
            }
        }
        initObservers()
    }

    private fun initObservers() {
        audioPlayerService?.listOfSongsLiveData?.observe(viewLifecycleOwner, { resultList ->
            if (resultList.isNotEmpty()) {
                binding.recyclerViewSearch.apply {
                    searchFragmentAdapter = SearchFragmentAdapter(resultList).apply {
                        onItemClickListener = { position ->
                            audioPlayerService?.setCurrentWindowIndex(position)
                        }
                        notifyDataSetChanged()
                    }
                    adapter = searchFragmentAdapter
                }
            }
        })
    }

    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)

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

    private fun bindToAudioService() {
        if (audioPlayerService == null) {
            activity?.bindService(
                Intent(this.context, AudioPlayerService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}