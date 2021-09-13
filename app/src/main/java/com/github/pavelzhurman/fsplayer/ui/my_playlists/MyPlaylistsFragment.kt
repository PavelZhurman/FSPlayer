package com.github.pavelzhurman.fsplayer.ui.my_playlists

import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentMyPlaylistsBinding
import com.github.pavelzhurman.musicdatabase.MAIN_PLAYLIST_ID
import com.google.android.material.snackbar.Snackbar

class MyPlaylistsFragment : BaseFragment<FragmentMyPlaylistsBinding>() {

    private var myPlaylistsAdapter: MyPlaylistsAdapter? = null

    val viewModel: MyPlaylistsViewModel by activityViewModels()

    private fun initObservers() {
        viewModel.listOfPlaylistsLiveData.observe(this, { list ->
            myPlaylistsAdapter = MyPlaylistsAdapter(list).apply {
                onPlaylistItemClickListener = { playlistItem ->
                    if (playlistItem.playlistId == MAIN_PLAYLIST_ID) {
                        Snackbar.make(
                            binding.recyclerViewMyPlaylists,
                            R.string.you_cannot_edit_main_playlist,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.sendPlaylist(playlistItem)
                        navigateToPlaylistEditor()
                    }
                }
            }
            binding.recyclerViewMyPlaylists.adapter = myPlaylistsAdapter
            binding.recyclerViewMyPlaylists.adapter?.notifyDataSetChanged()

        })
    }

    private fun navigateToPlaylistEditor() {
        Navigation.findNavController(requireView())
            .navigate(R.id.playlistEditorFragment)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getListOfPlaylists()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListOfPlaylists()
    }

    override fun initViews() {
        initObservers()
        viewModel.getListOfPlaylists()
        with(binding) {
            recyclerViewMyPlaylists.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

            fabAddPlaylist.setOnClickListener {
                AddPlaylistDialogFragment().show(
                    childFragmentManager,
                    null
                )
            }
        }
    }

    override fun getViewBinding() = FragmentMyPlaylistsBinding.inflate(layoutInflater)
}