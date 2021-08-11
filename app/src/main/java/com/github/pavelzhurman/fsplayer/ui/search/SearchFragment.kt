package com.github.pavelzhurman.fsplayer.ui.search

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentSearchBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override fun initViews() {
        with(binding) {}
    }

    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)
}