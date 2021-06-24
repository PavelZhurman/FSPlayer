package com.github.pavelzhurman.fsplayer.ui.search

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R

class SearchFragment : BaseFragment<SearchViewModel>() {

    override fun initObservers(viewModel: SearchViewModel) {}

    override fun initViews() {}

    override val viewModelClass: Class<SearchViewModel> = SearchViewModel::class.java
    override val layout: Int = R.layout.fragment_search
}