package com.github.pavelzhurman.fsplayer.ui.my_playlists

import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentMyPlaylistsBinding

class MyPlaylistsFragment : BaseFragment<FragmentMyPlaylistsBinding>() {

    private var recyclerView: RecyclerView? = null

    override fun initViews() {
        with(binding) {
            recyclerView = recyclerViewMyPlaylists
        }
    }

    override fun getViewBinding() = FragmentMyPlaylistsBinding.inflate(layoutInflater)
}