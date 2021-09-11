package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepositoryImpl2
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MyPlaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private var disposable: Disposable? = null

    private val musicDatabaseRepositoryImpl: MusicDatabaseRepositoryImpl2 =
        MusicDatabaseRepositoryImpl2(application.applicationContext)

    private val mutableListOfPlaylistsLiveData = MutableLiveData<List<PlaylistItem>>()
    val listOfPlaylistsLiveData: LiveData<List<PlaylistItem>>
        get() = mutableListOfPlaylistsLiveData

    fun getListOfPlaylists() {
        disposable =
            musicDatabaseRepositoryImpl.getListOfAllPlaylists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { listOfPlaylists ->
                    mutableListOfPlaylistsLiveData.value = listOfPlaylists
                }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}