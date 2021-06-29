package com.github.pavelzhurman.ui.player

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import android.widget.ImageView
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.pavelzhurman.ui.R


class PlayerView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val textViewArtist: TextView
    private val imageAlbum: ImageView
    private val textViewSongName: TextView
    private val imagePrevious: ImageButton
    private val imagePlay: ImageButton
    private val imageNext: ImageButton
    private val progressBar: AppCompatSeekBar

    init {
        inflate(context, R.layout.view_player, this)

        imageAlbum = findViewById(R.id.image_album)
        textViewArtist = findViewById(R.id.text_view_artist)
        textViewSongName = findViewById(R.id.text_view_song_name)
        imagePrevious = findViewById(R.id.image_button_previous)
        imagePlay = findViewById(R.id.image_button_play)
        imageNext = findViewById(R.id.image_button_next)
        progressBar = findViewById(R.id.progress_bar)

        imagePlay.setOnClickListener {
            progressBar.incrementProgressBy(1)
            val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_pause_24)
            imagePlay.setImageDrawable(drawable)
        }
    }

    fun setArtist(artist: String) {
        textViewArtist.text = artist
    }

    fun setSongName(songName: String) {
        textViewSongName.text = songName
    }

    fun getImageAlbum(): ImageView = imageAlbum

}