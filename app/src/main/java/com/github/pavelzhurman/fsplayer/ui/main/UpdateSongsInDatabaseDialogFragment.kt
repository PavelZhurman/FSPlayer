package com.github.pavelzhurman.fsplayer.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.DialogFragmentUpdateSongsInDatabaseBinding

class UpdateSongsInDatabaseDialogFragment : DialogFragment() {

    val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogFragmentUpdateSongsInDatabaseBinding.inflate(layoutInflater)

        with(binding) {
            buttonCancel.setOnClickListener { navigateToMainFragment() }
            buttonUpdate.setOnClickListener {
                viewModel.updateSongsInDatabase()
                viewModel.getListOfPlaylists()
                viewModel.getFavouriteSongs()
                viewModel.getCurrentPlaylist()
                navigateToMainFragment()

            }
        }

        return binding.root
    }

    private fun navigateToMainFragment() {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_main_fragment_container
        ).navigate(R.id.mainFragment)
    }

}