package com.github.pavelzhurman.fsplayer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemFavouriteSongMainFragmentBinding
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class FavouriteSongsAdapter(private var list: List<SongItem>) :
    RecyclerView.Adapter<FavouriteSongsAdapter.FavouriteSongsViewHolder>() {

    lateinit var onFavouriteSongItemClickListener: (songItem: SongItem) -> Unit

    inner class FavouriteSongsViewHolder(private val itemFavouriteSongMainFragmentBinding: ItemFavouriteSongMainFragmentBinding) :
        RecyclerView.ViewHolder(itemFavouriteSongMainFragmentBinding.root) {
        fun bind(songItem: SongItem) {
            with(itemFavouriteSongMainFragmentBinding) {
                textViewArtist.text = songItem.artist
                textViewSongName.text = songItem.title
                ImageLoader().loadPoster(this.root.context, songItem.albumUri, imageViewThumbnail)
                imageViewThumbnail.setOnClickListener {
                    onFavouriteSongItemClickListener.invoke(songItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteSongsViewHolder =
        FavouriteSongsViewHolder(
            ItemFavouriteSongMainFragmentBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    override fun onBindViewHolder(holder: FavouriteSongsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}