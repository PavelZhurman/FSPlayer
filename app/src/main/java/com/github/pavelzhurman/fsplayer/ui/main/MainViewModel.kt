package com.github.pavelzhurman.fsplayer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepository
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    private var disposable: Disposable? = null

    private val musicDatabaseRepositoryImpl: MusicDatabaseRepository =
        MusicDatabaseRepository(application.applicationContext)

    private val mutableListOfPlaylistsLiveData = MutableLiveData<List<PlaylistItem>>()
    val listOfPlaylistsLiveData: LiveData<List<PlaylistItem>>
        get() = mutableListOfPlaylistsLiveData

    private val mutableFavouriteSongsLiveData = MutableLiveData<List<SongItem>>()
    val favouriteSongsLiveData: LiveData<List<SongItem>>
        get() = mutableFavouriteSongsLiveData

    fun getListOfPlaylists() {
        disposable =
            musicDatabaseRepositoryImpl.getListOfAllPlaylists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { listOfPlaylists ->
                    mutableListOfPlaylistsLiveData.value = listOfPlaylists
                }
    }

    fun getFavouriteSongs() {
        disposable = musicDatabaseRepositoryImpl.getFavouriteSongs().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { favouriteSongs ->
                mutableFavouriteSongsLiveData.value = favouriteSongs
            }
    }

    fun collectAudioAndAddToMainPlaylist() {
        musicDatabaseRepositoryImpl.collectAudioAndAddToMainPlaylist()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}