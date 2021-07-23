package com.github.pavelzhurman.fsplayer.ui.main


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentMainBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> openSearchFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initObservers(viewModel: MainViewModel) {
    }

    override fun initViews() {
        with(binding) {
            textViewFavouriteSongs.setOnClickListener { openFavouriteSongsFragment() }
            textViewMyPlaylists.setOnClickListener { openMyPlaylistFragment() }
        }
    }

    private fun openSearchFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_searchFragment)
    }

    private fun openFavouriteSongsFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_favouriteSongsFragment)
    }

    private fun openMyPlaylistFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_mainFragment_to_myPlaylistsFragment)
    }

    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    override fun getViewBinding() = FragmentMainBinding.inflate(layoutInflater)
}