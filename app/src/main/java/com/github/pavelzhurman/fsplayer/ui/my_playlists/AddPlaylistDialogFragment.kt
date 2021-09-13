package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.DialogFragmentAddPlaylistBinding
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.google.android.material.snackbar.Snackbar

const val ADD_PLAYLIST_ID = 2L

class AddPlaylistDialogFragment :
    DialogFragment() {

    val viewModel: MyPlaylistsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogFragmentAddPlaylistBinding.inflate(layoutInflater)

        with(binding) {
            textViewTitle.text = getString(R.string.enter_playlist_name)
            buttonCancel.setOnClickListener { dismiss() }
            buttonAddNewPlaylist.setOnClickListener {
                if (editTextPlaylistName.text.isNullOrEmpty()) {
                    Snackbar.make(
                        root,
                        getString(R.string.enter_playlist_name_please),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                } else {
                    viewModel.addPlaylist(
                        PlaylistItem(
                            playlistId = ADD_PLAYLIST_ID,
                            name = editTextPlaylistName.text.toString(),
                            currentPlaylist = false
                        )
                    )
                    dismiss()
                }
            }
        }
        return binding.root
    }
}