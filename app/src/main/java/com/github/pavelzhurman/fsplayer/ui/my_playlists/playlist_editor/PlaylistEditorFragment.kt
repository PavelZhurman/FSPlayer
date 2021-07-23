package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlaylistEditorBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class PlaylistEditorFragment :
    BaseFragment<FragmentPlaylistEditorBinding, PlaylistEditorViewModel>() {
    override fun initObservers(viewModel: PlaylistEditorViewModel) {}

    override fun initViews() {
        with(binding) {}
    }

    override val viewModelClass: Class<PlaylistEditorViewModel> =
        PlaylistEditorViewModel::class.java

    override fun getViewBinding() = FragmentPlaylistEditorBinding.inflate(layoutInflater)
}