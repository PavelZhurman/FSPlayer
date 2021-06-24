package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R

class PlaylistEditorFragment : BaseFragment<PlaylistEditorViewModel>() {
    override fun initObservers(viewModel: PlaylistEditorViewModel) {}

    override fun initViews() {}

    override val viewModelClass: Class<PlaylistEditorViewModel> =
        PlaylistEditorViewModel::class.java
    override val layout: Int = R.layout.fragment_playlist_editor
}