package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

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
import com.github.pavelzhurman.fsplayer.databinding.DialogFragmentDeletePlaylistBinding
import com.github.pavelzhurman.fsplayer.ui.my_playlists.MyPlaylistsViewModel

class DeletePlaylistDialogFragment : DialogFragment() {

    val viewModel: MyPlaylistsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DialogFragmentDeletePlaylistBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        with(binding) {
            buttonCancel.setOnClickListener { navigateToPlaylistEditorFragment() }
            buttonDelete.setOnClickListener {
                viewModel.playlistItemLiveData.value?.playlistId?.let { playlistId ->
                    viewModel.deletePlaylist(playlistId)
                    navigateToMyPlaylistsFragment()
                }
            }
        }

        return binding.root
    }

    private fun navigateToPlaylistEditorFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_main_fragment_container)
            .navigate(R.id.action_deletePlaylistDialogFragment_to_playlistEditorFragment)
    }

    private fun navigateToMyPlaylistsFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_main_fragment_container)
            .navigate(R.id.action_deletePlaylistDialogFragment_to_myPlaylistsFragment)
    }
}