package com.github.pavelzhurman.fsplayer
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.github.pavelzhurman.core.Stubs
//import com.github.pavelzhurman.fsplayer.databinding.ActivityMainBinding
//import com.github.pavelzhurman.image_loader.ImageLoader
//
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val url = Stubs.Images().FAKE_POSTER_FREESOUND_SOUND
//        val url2 = Stubs.Images().FAKE_POSTER_TREE
//        val url3 = Stubs.Images().FAKE_POSTER_NYAN_CAT
//
//        val miniPlayerBinding = binding.miniPlayerView.getBinding()
//
//        miniPlayerBinding?.apply {
//            textViewArtist.text = Stubs.Texts().ARTIST_EMINEM
//            textViewSongName.text = Stubs.Texts().SONG_LOSE_YOURSELF
//
//            ImageLoader().loadPoster(this@MainActivity, url3, imageAlbum)
//        }
//        miniPlayerBinding?.seekBar?.progress = 15
//    }
//}