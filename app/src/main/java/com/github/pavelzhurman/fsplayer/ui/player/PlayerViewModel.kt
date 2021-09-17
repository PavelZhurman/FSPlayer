package com.github.pavelzhurman.fsplayer.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.core.ProjectConstants.FAVOURITE_PLAYLIST_ID
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepository
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val musicDatabaseRepository = MusicDatabaseRepository(application.applicationContext)

    private val listOfFavouriteSongsMutableLiveData = MutableLiveData<MutableList<SongItem>>()
    val listOfFavouriteSongsLiveData: LiveData<MutableList<SongItem>>
        get() = listOfFavouriteSongsMutableLiveData

    fun getListOfFavouriteSongs() {
        musicDatabaseRepository.getSongsFromPlaylistByPlaylistId(FAVOURITE_PLAYLIST_ID)
            .subscribe { list ->
                listOfFavouriteSongsMutableLiveData.value = list as MutableList<SongItem>?
            }
    }

    fun addSongToFavouritePlaylist(songItem: SongItem) {
        musicDatabaseRepository.addFavouriteSong(songItem.songId)
    }

    fun removeSongFromFavouritePlaylist(songItem: SongItem) {
        musicDatabaseRepository.deleteFavouriteSong(songItem.songId)
    }

}