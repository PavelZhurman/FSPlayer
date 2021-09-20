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
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.ProjectConstants.FAVOURITE_PLAYLIST_ID
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.exoplayer.AudioPlayerService
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentMainBinding
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

const val FAVOURITE_PLAYLIST_INDEX_IN_ADAPTER = 1

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private var audioPlayerService: AudioPlayerService? = null

    private var playlistsMainFragmentAdapter: MyPlaylistsMainFragmentAdapter? = null

    private var currentPlaylist: PlaylistItem? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }

    }

    val viewModel: MainViewModel by activityViewModels()

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

    private fun visibilityOfTextViewNoSounds(currentPlaylist: PlaylistItem?) {
        binding.textViewNoSoundsFound.visibility = if (currentPlaylist == null) {
            VISIBLE
        } else {
            INVISIBLE
        }
    }

    private fun visibilityOfTextViewNoSounds(listOfPlaylists: List<PlaylistItem>) {
        binding.textViewNoSoundsFound.visibility = if (listOfPlaylists.isNullOrEmpty()) {
            VISIBLE
        } else {
            INVISIBLE
        }
    }

    private fun initRecyclerViewMyPlaylists(
        listOfPlaylists: List<PlaylistItem>,
        currentPlaylist: PlaylistItem
    ) {
        binding.recyclerViewMyPlaylists.apply {
            playlistsMainFragmentAdapter =
                MyPlaylistsMainFragmentAdapter(
                    listOfPlaylists,
                    currentPlaylist
                ).apply {
                    onPlaylistItemClickListener = { playlistItem ->
                        if (audioPlayerService == null) {
                            bindToAudioService()
                        }
                        audioPlayerService?.setSource(playlistItem.playlistId, null)
                        viewModel.setCurrentPlaylist(playlistItem)
                    }
                }
            adapter = playlistsMainFragmentAdapter
        }
    }

    private fun initRecyclerViewFavouriteSongs(favouriteSongsList: List<SongItem>) {
        binding.recyclerViewFavouriteSongs.apply {
            adapter = FavouriteSongsAdapter(favouriteSongsList).apply {

                onFavouriteSongItemClickListener = { songItem ->
                    audioPlayerService?.setSource(FAVOURITE_PLAYLIST_ID, songItem)

                    playlistsMainFragmentAdapter?.selectedItemPosition?.let {
                        binding.recyclerViewMyPlaylists.adapter?.notifyItemChanged(it)
                    }
                    binding.recyclerViewMyPlaylists.adapter?.notifyItemChanged(
                        FAVOURITE_PLAYLIST_INDEX_IN_ADAPTER
                    )
                    playlistsMainFragmentAdapter?.selectedItemPosition =
                        FAVOURITE_PLAYLIST_INDEX_IN_ADAPTER
                }
            }

            if (favouriteSongsList.isNotEmpty()) {
                binding.textViewNoFavouriteSongsAdded.visibility = INVISIBLE
            } else {
                binding.textViewNoFavouriteSongsAdded.visibility = VISIBLE
            }

        }
    }

    private fun initObservers() {
        viewModel.currentPlaylistLiveData.observe(this, { currentPlaylistItem ->
            currentPlaylist = currentPlaylistItem
            viewModel.listOfPlaylistsLiveData.observe(viewLifecycleOwner, { listOfPlaylists ->

                visibilityOfTextViewNoSounds(listOfPlaylists)
                initRecyclerViewMyPlaylists(listOfPlaylists, currentPlaylistItem)

            })
        })

        viewModel.favouriteSongsLiveData.observe(viewLifecycleOwner, { favouriteSongsList ->
            initRecyclerViewFavouriteSongs(favouriteSongsList)
        })
    }


    override fun onResume() {
        super.onResume()
        visibilityOfTextViewNoSounds(currentPlaylist)

        bindToAudioService()
        viewModel.getFavouriteSongs()
        viewModel.getListOfPlaylists()
        viewModel.getCurrentPlaylist()
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
            textViewMyPlaylists.setOnClickListener { openMyPlaylistFragment() }
        }
        viewModel.getFavouriteSongs()
        viewModel.getListOfPlaylists()
        viewModel.getCurrentPlaylist()
    }

    private fun openSearchFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_searchFragment)
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