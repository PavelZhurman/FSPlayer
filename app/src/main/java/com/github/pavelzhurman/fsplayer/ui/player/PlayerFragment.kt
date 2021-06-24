package com.github.pavelzhurman.fsplayer.ui.player

import android.os.Bundle
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R

class PlayerFragment : BaseFragment<PlayerViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun initObservers(viewModel: PlayerViewModel) {}

    override fun initViews() {}

    override val viewModelClass: Class<PlayerViewModel> = PlayerViewModel::class.java
    override val layout: Int = R.layout.fragment_player
}