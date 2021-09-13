package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemPlaylistEditorBinding
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class PlaylistEditorAdapter(private val listOfAllSongs: MutableList<SongItem>) :
    RecyclerView.Adapter<PlaylistEditorAdapter.PlaylistEditorViewHolder>() {

    lateinit var onRemoveClickListener: (songItem: SongItem) -> Unit

    inner class PlaylistEditorViewHolder(private val itemPlaylistEditorBinding: ItemPlaylistEditorBinding) :
        RecyclerView.ViewHolder(itemPlaylistEditorBinding.root) {
        fun bind(songItem: SongItem, position: Int) {
            with(itemPlaylistEditorBinding) {
                textViewArtist.text = songItem.artist
                textViewTitle.text = songItem.title
                imageButtonRemove.setOnClickListener {
                    onRemoveClickListener.invoke(songItem)
                    listOfAllSongs.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistEditorViewHolder =
        PlaylistEditorViewHolder(
            ItemPlaylistEditorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PlaylistEditorViewHolder, position: Int) {
        holder.bind(listOfAllSongs[position], position)
    }

    override fun getItemCount(): Int = listOfAllSongs.size


}