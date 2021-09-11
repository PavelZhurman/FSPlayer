package com.github.pavelzhurman.fsplayer.ui.main


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentMainBinding
import com.github.pavelzhurman.musicdatabase.FAVOURITE_PLAYLIST_ID

const val FAVOURITE_PLAYLIST_INDEX_IN_ADAPTER = 1

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private var audioPlayerService: AudioPlayerService? = null

    private var playlistsAdapter: MyPlaylistsAdapter? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }

    }

    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)
            .get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        bindToAudioService()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> openSearchFragment()
            R.id.app_bar_update -> {
                viewModel.collectAudioAndAddToMainPlaylist()
                viewModel.getListOfPlaylists()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initObservers() {
        viewModel.listOfPlaylistsLiveData.observe(viewLifecycleOwner, { listOfPlaylists ->
            if (listOfPlaylists.isEmpty()){
                binding.textViewNoSoundsFound.visibility = VISIBLE
            } else {
                binding.textViewNoSoundsFound.visibility = INVISIBLE
            }
            binding.recyclerViewMyPlaylists.apply {
                playlistsAdapter = MyPlaylistsAdapter(listOfPlaylists).apply {
                    onPlaylistItemClickListener = { playlistItem ->
                        audioPlayerService?.setSource(playlistItem.playlistId, null)
                    }
                }
                adapter = playlistsAdapter
            }
        })

        viewModel.favouriteSongsLiveData.observe(viewLifecycleOwner, { favouriteSongsList ->
            binding.recyclerViewFavouriteSongs.apply {
                adapter = FavouriteSongsAdapter(favouriteSongsList).apply {
                    onFavouriteSongItemClickListener = { songItem ->
                        audioPlayerService?.setSource(FAVOURITE_PLAYLIST_ID, songItem)
                        playlistsAdapter?.selectedItemPosition =
                            FAVOURITE_PLAYLIST_INDEX_IN_ADAPTER
                        binding.recyclerViewMyPlaylists.adapter?.notifyDataSetChanged()
                    }
                }
                if (favouriteSongsList.isNotEmpty()) {
                    binding.textViewNoFavouriteSongsAdded.visibility = INVISIBLE
                } else {
                    binding.textViewNoFavouriteSongsAdded.visibility = VISIBLE
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavouriteSongs()
        viewModel.getListOfPlaylists()
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

    override fun initViews() {
        initObservers()
        with(binding) {
            textViewFavouriteSongs.setOnClickListener { openFavouriteSongsFragment() }
            textViewMyPlaylists.setOnClickListener { openMyPlaylistFragment() }
        }
        viewModel.getFavouriteSongs()
        viewModel.getListOfPlaylists()
    }

    private fun openSearchFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_searchFragment)
    }

    private fun openFavouriteSongsFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_favouriteSongsFragment)
    }

    private fun openMyPlaylistFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_myPlaylistsFragment)
    }

    override fun getViewBinding() = FragmentMainBinding.inflate(layoutInflater)

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