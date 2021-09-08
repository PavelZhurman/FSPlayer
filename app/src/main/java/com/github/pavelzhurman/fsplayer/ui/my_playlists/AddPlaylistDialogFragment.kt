package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.DialogFragmentAddPlaylistBinding
import com.google.android.material.snackbar.Snackbar

class AddPlaylistDialogFragment :
    DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle(getString(R.string.enter_playlist_name))
        val binding = DialogFragmentAddPlaylistBinding.inflate(layoutInflater)
        with(binding) {
            buttonCancel.setOnClickListener { dismiss() }
            buttonAddNewPlaylist.setOnClickListener {
                if (editTextPlaylistName.text.isNullOrEmpty()) {
                    Snackbar.make(root, getString(R.string.enter_playlist_name_please), Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Snackbar.make(root, getString(R.string.not_implemented_yet), Snackbar.LENGTH_SHORT)
                        .show()
                    // TODO
                }
            }
        }
        return binding.root
    }
}