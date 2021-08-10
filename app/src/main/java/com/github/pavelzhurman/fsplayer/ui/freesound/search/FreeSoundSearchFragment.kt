package com.github.pavelzhurman.fsplayer.ui.freesound.search

import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundSearchBinding

class FreeSoundSearchFragment :
    BaseFragment<FragmentFreeSoundSearchBinding, FreeSoundSearchViewModel>() {

    private var freesoundSearchAdapter: FreesoundSearchDataItemAdapter =
        FreesoundSearchDataItemAdapter()
    private var query: String = "1234"

    override fun initObservers(viewModel: FreeSoundSearchViewModel) {
        viewModel.apply {
            freesoundSongItemListLiveData.observe(
                viewLifecycleOwner,
                { data -> setValuesInAdapter(data) }
            )
            fetchFreesoundSearchData(query)
        }
    }

    private fun setValuesInAdapter(list: List<FreesoundSongItem>) {
        freesoundSearchAdapter.values = list
        freesoundSearchAdapter.notifyDataSetChanged()
    }

    override fun initViews() {
        with(binding) {
            recyclerView.apply {
                adapter = freesoundSearchAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override val viewModelClass: Class<FreeSoundSearchViewModel> =
        FreeSoundSearchViewModel::class.java

    override fun getViewBinding() = FragmentFreeSoundSearchBinding.inflate(layoutInflater)
}