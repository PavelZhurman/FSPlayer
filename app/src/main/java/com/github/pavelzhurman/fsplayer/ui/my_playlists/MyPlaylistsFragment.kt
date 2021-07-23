package com.github.pavelzhurman.fsplayer.ui.my_playlists

import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentMyPlaylistsBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class MyPlaylistsFragment : BaseFragment<FragmentMyPlaylistsBinding, MyPlaylistsViewModel>() {

    private var recyclerView: RecyclerView? = null

    override fun initObservers(viewModel: MyPlaylistsViewModel) {}

    override fun initViews() {
        with(binding) {
            recyclerView = recyclerViewMyPlaylists
        }
    }

    override val viewModelClass: Class<MyPlaylistsViewModel> = MyPlaylistsViewModel::class.java
    override fun getViewBinding() = FragmentMyPlaylistsBinding.inflate(layoutInflater)
}