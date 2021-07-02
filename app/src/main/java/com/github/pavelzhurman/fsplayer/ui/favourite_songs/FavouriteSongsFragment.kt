package com.github.pavelzhurman.fsplayer.ui.favourite_songs

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentFavouriteSongsBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class FavouriteSongsFragment :
    BaseFragment<FragmentFavouriteSongsBinding, FavouriteSongsViewModel>() {

    override fun initObservers(viewModel: FavouriteSongsViewModel) {}

    override fun initViews() {
        with(binding) {
            player.setOnClickListener { PlayerActivity.start(requireContext()) }
        }
    }

    override val viewModelClass: Class<FavouriteSongsViewModel> =
        FavouriteSongsViewModel::class.java

    override fun getViewBinding() = FragmentFavouriteSongsBinding.inflate(layoutInflater)
}