package com.github.pavelzhurman.fsplayer.ui.player

import android.os.Bundle
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlayerBinding

class PlayerFragment : BaseFragment<FragmentPlayerBinding,PlayerViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun initObservers(viewModel: PlayerViewModel) {}

    override fun initViews() {}

    override val viewModelClass: Class<PlayerViewModel> = PlayerViewModel::class.java
    override fun getViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)
}