package com.github.pavelzhurman.fsplayer.ui.favourite_songs

import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentFavouriteSongsBinding

class FavouriteSongsFragment :
    BaseFragment<FragmentFavouriteSongsBinding>() {

    override fun initViews() {}

    override fun getViewBinding() = FragmentFavouriteSongsBinding.inflate(layoutInflater)
}