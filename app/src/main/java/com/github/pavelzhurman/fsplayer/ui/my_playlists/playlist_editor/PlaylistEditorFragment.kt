package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlaylistEditorBinding

class PlaylistEditorFragment :
    BaseFragment<FragmentPlaylistEditorBinding>() {

    override fun initViews() {
        with(binding) {}
    }

    override fun getViewBinding() = FragmentPlaylistEditorBinding.inflate(layoutInflater)
}