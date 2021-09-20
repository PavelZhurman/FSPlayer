package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlaylistEditorBinding
import com.github.pavelzhurman.fsplayer.ui.my_playlists.MyPlaylistsViewModel
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class PlaylistEditorFragment :
    BaseFragment<FragmentPlaylistEditorBinding>() {

    val viewModel: MyPlaylistsViewModel by activityViewModels()

    override fun initViews() {
        initObservers()
        with(binding) {
            fabAddSong.setOnClickListener { navigateToAddSongsToPlaylistFragment() }
        }
    }

    private fun navigateToAddSongsToPlaylistFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_playlistEditorFragment_to_addSongsToPlaylistFragment)
    }

    private fun initObservers() {
        viewModel.playlistItemLiveData.observe(this, { playlistItem ->
            viewModel.getSongsFromPlaylistById(playlistItem.playlistId).subscribe { listOfSongs ->
                binding.recyclerViewPlaylistEditor.adapter =
                    PlaylistEditorAdapter(listOfSongs as MutableList<SongItem>).apply {
                        onRemoveClickListener = { songItem ->
                            viewModel.deleteFromPlaylist(playlistItem.playlistId, songItem.songId)
                        }
                    }
                binding.recyclerViewPlaylistEditor.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun openDeletePlaylistDialogFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.deletePlaylistDialogFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlist_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_delete_playlist -> openDeletePlaylistDialogFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewBinding() = FragmentPlaylistEditorBinding.inflate(layoutInflater)
}