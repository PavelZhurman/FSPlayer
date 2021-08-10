package com.github.pavelzhurman.fsplayer.ui.freesound.search

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundSearchBinding
import com.github.pavelzhurman.fsplayer.di.App
import com.github.pavelzhurman.fsplayer.ui.freesound.FreesoundSearchViewModelFactory
import javax.inject.Inject

class FreeSoundSearchFragment :
    BaseFragment<FragmentFreeSoundSearchBinding>() {

    @Inject
    lateinit var freesoundViewModelFactory: FreesoundSearchViewModelFactory<FreeSoundSearchViewModel>

    val viewModel: FreeSoundSearchViewModel by lazy {
        ViewModelProvider(this, freesoundViewModelFactory)
            .get(FreeSoundSearchViewModel::class.java)
    }

    private var freesoundSearchAdapter: FreesoundSearchDataItemAdapter =
        FreesoundSearchDataItemAdapter()
    private var query: String = "1234"

    private fun initObservers(viewModel: FreeSoundSearchViewModel) {
        viewModel.apply {
            freesoundSongItemListLiveData.observe(
                viewLifecycleOwner,
                { data -> setValuesInAdapter(data) }
            )
        }
    }

    private fun setValuesInAdapter(list: List<FreesoundSongItem>) {
        freesoundSearchAdapter.values = list
    }

    override fun initViews() {
        App.appComponent.inject(this)
        with(binding) {
            recyclerView.apply {
                adapter = freesoundSearchAdapter
                layoutManager = LinearLayoutManager(context)
            }

        }
        initButtonSearch()
        initObservers(viewModel)
    }

    private fun initButtonSearch() {
        with(binding) {
            buttonSearch.setOnClickListener {
                query = editTextSearchBy.text.toString()
                viewModel.fetchFreesoundSearchData(query)
            }
        }
    }

    override fun getViewBinding() = FragmentFreeSoundSearchBinding.inflate(layoutInflater)
}