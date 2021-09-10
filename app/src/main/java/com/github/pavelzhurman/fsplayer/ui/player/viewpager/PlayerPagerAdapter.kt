package com.github.pavelzhurman.fsplayer.ui.player.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.ItemPagerPlayerBinding
import com.github.pavelzhurman.fsplayer.ui.freesound.NOT_DOWNLOADED_SONG_ID
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepositoryImpl2
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import com.google.android.material.snackbar.Snackbar

class PlayerPagerAdapter(private val listOfSongs: List<SongItem>) :
    RecyclerView.Adapter<PlayerPagerAdapter.PlayerPagerViewHolder>() {

    private val imageLoader = ImageLoader()

    inner class PlayerPagerViewHolder(private val itemPagerPlayerBinding: ItemPagerPlayerBinding) :
        RecyclerView.ViewHolder(itemPagerPlayerBinding.root) {
        private val drawableNotFavourite = AppCompatResources.getDrawable(
            itemPagerPlayerBinding.root.context,
            R.drawable.ic_baseline_favorite_border_24
        )
        private val drawableFavourite = AppCompatResources.getDrawable(
            itemPagerPlayerBinding.root.context,
            R.drawable.ic_baseline_favorite_24
        )
        private val musicRepository =
            MusicDatabaseRepositoryImpl2(itemPagerPlayerBinding.root.context)

        fun bind(songItem: SongItem) {
            with(itemPagerPlayerBinding) {
                textViewArtist.text = songItem.artist
                textViewArtist.isSelected = true

                textViewSong.text = songItem.title
                textViewSong.isSelected = true

                imageLoader.loadPoster(root.context, songItem.albumUri, imageViewPoster)
                if (songItem.isFavourite) {
                    imageLoader.loadDrawable(
                        imageButtonFavourite,
                        drawableFavourite,
                        imageButtonFavourite
                    )
                } else {
                    imageLoader.loadDrawable(
                        imageButtonFavourite,
                        drawableNotFavourite,
                        imageButtonFavourite
                    )
                }

                imageButtonFavourite.setOnClickListener {
                    if (songItem.songId != NOT_DOWNLOADED_SONG_ID) {
                        songItem.isFavourite = !songItem.isFavourite

                        if (songItem.isFavourite) {
                            imageLoader.loadDrawable(
                                imageButtonFavourite,
                                drawableFavourite,
                                imageButtonFavourite
                            )
                            musicRepository.addFavouriteSong(songItem.songId)
                        } else {
                            imageLoader.loadDrawable(
                                imageButtonFavourite,
                                drawableNotFavourite,
                                imageButtonFavourite
                            )
                            musicRepository.deleteFavouriteSong(songItem.songId)

                        }
                        musicRepository.updateSong(songItem)
                    } else {
                        Snackbar.make(root, R.string.you_cannot_add_unloaded_songs_to_favourite, Snackbar.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPagerViewHolder =
        PlayerPagerViewHolder(
            ItemPagerPlayerBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: PlayerPagerViewHolder, position: Int) {
        holder.bind(listOfSongs[position])
    }

    override fun getItemCount(): Int = listOfSongs.size

}