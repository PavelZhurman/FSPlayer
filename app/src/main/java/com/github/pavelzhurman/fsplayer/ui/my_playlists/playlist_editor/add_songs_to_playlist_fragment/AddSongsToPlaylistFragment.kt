package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor.add_songs_to_playlist_fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentAddSongsToPlaylistBinding
import com.github.pavelzhurman.fsplayer.ui.my_playlists.MyPlaylistsViewModel
import com.github.pavelzhurman.musicdatabase.MAIN_PLAYLIST_ID
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class AddSongsToPlaylistFragment : BaseFragment<FragmentAddSongsToPlaylistBinding>() {

    private val viewModel: MyPlaylistsViewModel by activityViewModels()

    private var listOfAllSongs: List<SongItem>? = null
    private var listOfSongsFromCurrentPlaylist: MutableList<SongItem>? = null
    private var playlistId: Long? = null

    override fun initViews() {
        with(binding) {

            viewModel.getSongsFromPlaylistById(MAIN_PLAYLIST_ID).subscribe { resultList ->
                listOfAllSongs = resultList as MutableList<SongItem>?

                viewModel.playlistItemLiveData.observe(this@AddSongsToPlaylistFragment,
                    { currentPlaylist ->
                        playlistId = currentPlaylist.playlistId
                        viewModel.getSongsFromPlaylistById(currentPlaylist.playlistId)
                            .subscribe { list ->
                                listOfSongsFromCurrentPlaylist = list as MutableList<SongItem>?
                                if (listOfAllSongs != null && listOfSongsFromCurrentPlaylist != null) {
                                    recyclerViewAddSongs.adapter = AddSongsToPlaylistAdapter(
                                        listOfAllSongs!!,
                                        listOfSongsFromCurrentPlaylist!!
                                    ).apply {
                                        onAddClickListener = { songItem, position ->
                                            if (listOfSongsFromCurrentPlaylist?.contains(songItem) == true) {
                                                listOfSongsFromCurrentPlaylist?.remove(songItem)

                                            } else {
                                                listOfSongsFromCurrentPlaylist?.add(songItem)
                                            }
                                            notifyItemChanged(position)
                                        }
                                    }

                                }
                            }
                    })
            }


        }
    }

    private fun applyAndBackToEditPlaylistsFragment() {
        playlistId?.let {
            listOfSongsFromCurrentPlaylist?.let { it1 ->
                viewModel.clearAndAddNewSongsToPlaylist(
                    it,
                    it1
                )
            }
        }
        Navigation.findNavController(requireView())
            .navigate(R.id.action_addSongsToPlaylistFragment_to_playlistEditorFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_songs_to_playlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_apply -> applyAndBackToEditPlaylistsFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewBinding() = FragmentAddSongsToPlaylistBinding.inflate(layoutInflater)
}