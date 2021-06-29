package com.github.pavelzhurman.fsplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.ui.player.PlayerView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger().logcatD("TagTag", localClassName)
        Logger().logcatE("TagTag", localClassName)

        val tvHelloWorld = findViewById<TextView>(R.id.tvHelloWorld)
        tvHelloWorld.text = getString(R.string.hello_world)

        val playerView: PlayerView = findViewById(R.id.player_view)
        playerView.setArtist("Hello")
        playerView.setSongName("It's me")

        val url = "https://freesound.org/data/displays/2/2223_3525_wave_L.png"
        Glide.with(this)
            .load(url)
            .into(playerView.getImageAlbum())
    }
}