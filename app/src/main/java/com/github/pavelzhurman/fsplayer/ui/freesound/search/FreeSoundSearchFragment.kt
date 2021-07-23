package com.github.pavelzhurman.fsplayer.ui.freesound.search

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundSearchBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class FreeSoundSearchFragment :
    BaseFragment<FragmentFreeSoundSearchBinding, FreeSoundSearchViewModel>() {
    override fun initObservers(viewModel: FreeSoundSearchViewModel) {}

    override fun initViews() {
        with(binding) {}
    }

    override val viewModelClass: Class<FreeSoundSearchViewModel> =
        FreeSoundSearchViewModel::class.java

    override fun getViewBinding() = FragmentFreeSoundSearchBinding.inflate(layoutInflater)
}