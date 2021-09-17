package com.github.pavelzhurman.fsplayer.ui.main

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemPlaylistMainFragmentBinding
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem

class MyPlaylistsMainFragmentAdapter(
    private val listOfPlaylists: List<PlaylistItem>,
    private val currentPlaylistId: PlaylistItem
) :
    RecyclerView.Adapter<MyPlaylistsMainFragmentAdapter.MyPlaylistsItemAdapterViewHolder>() {

    lateinit var onPlaylistItemClickListener: (playlistItem: PlaylistItem) -> Unit
    var selectedItemPosition: Int = listOfPlaylists.indexOf(currentPlaylistId)

    inner class MyPlaylistsItemAdapterViewHolder(private val itemPlaylistMainFragmentBinding: ItemPlaylistMainFragmentBinding) :
        RecyclerView.ViewHolder(itemPlaylistMainFragmentBinding.root) {

        fun bind(playlistItem: PlaylistItem) {

            with(itemPlaylistMainFragmentBinding) {
                textViewPlaylistName.text = playlistItem.name

                root.setOnClickListener {
                    notifyItemChanged(selectedItemPosition)
                    selectedItemPosition = bindingAdapterPosition
                    notifyItemChanged(selectedItemPosition)

                    onPlaylistItemClickListener.invoke(playlistItem)

                    playlistItem.currentPlaylist = true
                    imageViewCurrentPlaylist.visibility = VISIBLE

//                    notifyDataSetChanged()
                }

                if (selectedItemPosition == bindingAdapterPosition) {
                    imageViewCurrentPlaylist.visibility = VISIBLE
                } else imageViewCurrentPlaylist.visibility = INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyPlaylistsItemAdapterViewHolder =
        MyPlaylistsItemAdapterViewHolder(
            ItemPlaylistMainFragmentBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    override fun onBindViewHolder(holder: MyPlaylistsItemAdapterViewHolder, position: Int) {
        holder.bind(listOfPlaylists[position])
    }

    override fun getItemCount(): Int = listOfPlaylists.size
}