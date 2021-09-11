package com.github.pavelzhurman.fsplayer.ui.my_playlists

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentMyPlaylistsBinding
import com.github.pavelzhurman.fsplayer.ui.main.MainViewModel

class MyPlaylistsFragment : BaseFragment<FragmentMyPlaylistsBinding>() {

    val viewModel: MyPlaylistsViewModel by lazy {
        ViewModelProvider(this)
            .get(MyPlaylistsViewModel::class.java)
    }

    private fun initObservers(){
        viewModel.listOfPlaylistsLiveData.observe(this, { list ->

        })
    }

    override fun initViews() {
        viewModel.getListOfPlaylists()
        with(binding) {
            fabAddPlaylist.setOnClickListener {
                AddPlaylistDialogFragment().show(
                    childFragmentManager,
                    "add_playlist_dialog_fragment"
                )
            }
        }
    }

    override fun getViewBinding() = FragmentMyPlaylistsBinding.inflate(layoutInflater)
}