package com.github.pavelzhurman.fsplayer.ui.my_playlists.playlist_editor.add_songs_to_playlist_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemAddSongsToPlaylistBinding
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class AddSongsToPlaylistAdapter(
    private val list: List<SongItem>,
    private val listOfSongsFromCurrentPlaylist: List<SongItem>
) :
    RecyclerView.Adapter<AddSongsToPlaylistAdapter.AddSongsToPlaylistViewHolder>() {

    lateinit var onAddClickListener: (songItem: SongItem, position: Int) -> Unit

    inner class AddSongsToPlaylistViewHolder(private val itemAddSongsToPlaylistBinding: ItemAddSongsToPlaylistBinding) :
        RecyclerView.ViewHolder(itemAddSongsToPlaylistBinding.root) {

        private val drawableRemove = AppCompatResources.getDrawable(
            itemAddSongsToPlaylistBinding.root.context,
            com.github.pavelzhurman.ui.R.drawable.ic_baseline_remove_24
        )

        private val drawableAdd = AppCompatResources.getDrawable(
            itemAddSongsToPlaylistBinding.root.context,
            com.github.pavelzhurman.ui.R.drawable.ic_baseline_add_24
        )

        fun bind(songItem: SongItem) {
            with(itemAddSongsToPlaylistBinding) {
                textViewArtist.text = songItem.artist
                textViewTitle.text = songItem.title
                ImageLoader().loadPoster(root.context, songItem.albumUri, imageViewPoster)

                if (listOfSongsFromCurrentPlaylist.contains(songItem)) {
                    ImageLoader().loadDrawable(imageButtonAdd, drawableRemove, imageButtonAdd)
                } else {
                    ImageLoader().loadDrawable(imageButtonAdd, drawableAdd, imageButtonAdd)
                }

                imageButtonAdd.setOnClickListener {
                    onAddClickListener.invoke(songItem, bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddSongsToPlaylistViewHolder =
        AddSongsToPlaylistViewHolder(
            ItemAddSongsToPlaylistBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    override fun onBindViewHolder(holder: AddSongsToPlaylistViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}