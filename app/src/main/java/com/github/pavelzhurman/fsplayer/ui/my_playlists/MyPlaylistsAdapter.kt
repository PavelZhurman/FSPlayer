package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemPlaylistMainFragmentBinding
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem

class MyPlaylistsAdapter(private val list: List<PlaylistItem>) :
    RecyclerView.Adapter<MyPlaylistsAdapter.MyPlaylistsViewHolder>() {

    lateinit var onPlaylistItemClickListener: (playlistItem: PlaylistItem) -> Unit


    inner class MyPlaylistsViewHolder(private val itemPlaylistMainFragmentBinding: ItemPlaylistMainFragmentBinding) :
        RecyclerView.ViewHolder(itemPlaylistMainFragmentBinding.root) {

        fun bind(playlistItem: PlaylistItem) {
            with(itemPlaylistMainFragmentBinding) {
                textViewPlaylistName.text = playlistItem.name
                root.setOnClickListener { onPlaylistItemClickListener.invoke(playlistItem) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlaylistsViewHolder =
        MyPlaylistsViewHolder(
            ItemPlaylistMainFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )


    override fun onBindViewHolder(holder: MyPlaylistsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}