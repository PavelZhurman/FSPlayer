package com.github.pavelzhurman.fsplayer.ui.freesound.search

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R

class FreeSoundSearchFragment : BaseFragment<FreeSoundSearchViewModel>() {
    override fun initObservers(viewModel: FreeSoundSearchViewModel) {}

    override fun initViews() {}

    override val viewModelClass: Class<FreeSoundSearchViewModel> =
        FreeSoundSearchViewModel::class.java
    override val layout: Int = R.layout.fragment_free_sound_search
}