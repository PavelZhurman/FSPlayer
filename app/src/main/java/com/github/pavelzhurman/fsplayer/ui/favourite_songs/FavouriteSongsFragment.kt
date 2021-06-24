package com.github.pavelzhurman.fsplayer.ui.favourite_songs

import android.content.Intent
import android.widget.Button
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class FavouriteSongsFragment : BaseFragment<FavouriteSongsViewModel>() {

    private lateinit var buttonPlayer: Button

    override fun initObservers(viewModel: FavouriteSongsViewModel) {}

    override fun initViews() {
        
        buttonPlayer = view?.findViewById(R.id.player) ?: Button(context)

        buttonPlayer.setOnClickListener {
            val intentToStartPlayerActivity = Intent(context, PlayerActivity::class.java)
            startActivity(intentToStartPlayerActivity)
        }
    }

    override val viewModelClass: Class<FavouriteSongsViewModel> =
        FavouriteSongsViewModel::class.java
    override val layout: Int = R.layout.fragment_favourite_songs
}