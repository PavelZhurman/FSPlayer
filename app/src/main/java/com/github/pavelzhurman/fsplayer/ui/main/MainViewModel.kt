package com.github.pavelzhurman.fsplayer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepositoryImpl2
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    private var disposable: Disposable? = null

    private val musicDatabaseRepositoryImpl: MusicDatabaseRepositoryImpl2 =
        MusicDatabaseRepositoryImpl2(application.applicationContext)

    private val mutablePlaylistItemListLiveData = MutableLiveData<List<PlaylistItem>>()
    val playlistItemListLiveData: LiveData<List<PlaylistItem>>
        get() = mutablePlaylistItemListLiveData

    private val mutableFavouriteSongsLiveData = MutableLiveData<List<SongItem>>()
    val favouriteSongsLiveData: LiveData<List<SongItem>>
        get() = mutableFavouriteSongsLiveData

    fun getListOfPlaylists() {
        disposable =
            musicDatabaseRepositoryImpl.getListOfAllPlaylists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { listOfPlaylists ->
                    mutablePlaylistItemListLiveData.value = listOfPlaylists
                }
    }

    fun getFavouriteSongs() {
        disposable = musicDatabaseRepositoryImpl.getFavouriteSongs().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { favouriteSongs ->
                mutableFavouriteSongsLiveData.value = favouriteSongs
            }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}