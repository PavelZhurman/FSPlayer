package com.github.pavelzhurman.fsplayer.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class MainFragment : BaseFragment<MainViewModel>() {

    private lateinit var textViewFavouriteSongs: TextView
    private lateinit var textViewMuPlaylists: TextView
    private lateinit var buttonPlayer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_mainFragment_to_searchFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initObservers(viewModel: MainViewModel) {
    }

    override fun initViews() {
        textViewFavouriteSongs =
            view?.findViewById(R.id.text_view_favourite_songs) ?: TextView(context)
        textViewMuPlaylists = view?.findViewById(R.id.text_view_my_playlists) ?: TextView(context)
        buttonPlayer = view?.findViewById(R.id.player) ?: Button(context)

        textViewFavouriteSongs.setOnClickListener {
            view?.let { view ->
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_favouriteSongsFragment)
            }
        }
        textViewMuPlaylists.setOnClickListener {
            view?.let { view ->
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_myPlaylistsFragment)
            }
        }
        buttonPlayer.setOnClickListener {
            val intentToStartPlayerActivity = Intent(context, PlayerActivity::class.java)
            startActivity(intentToStartPlayerActivity)
        }

    }

    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    override val layout: Int = R.layout.fragment_main

}