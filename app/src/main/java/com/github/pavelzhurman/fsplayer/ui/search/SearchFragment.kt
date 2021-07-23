package com.github.pavelzhurman.fsplayer.ui.search

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentSearchBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    override fun initObservers(viewModel: SearchViewModel) {}

    override fun initViews() {
        with(binding) {}
    }

    override val viewModelClass: Class<SearchViewModel> = SearchViewModel::class.java
    override fun getViewBinding() = FragmentSearchBinding.inflate(layoutInflater)
}