package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepository
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MyPlaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private var disposable: Disposable? = null

    private val musicDatabaseRepositoryImpl: MusicDatabaseRepository =
        MusicDatabaseRepository(application.applicationContext)

    private val mutableListOfPlaylistsLiveData = MutableLiveData<List<PlaylistItem>>()
    val listOfPlaylistsLiveData: LiveData<List<PlaylistItem>>
        get() = mutableListOfPlaylistsLiveData

    private val mutablePlaylistItemLiveData = MutableLiveData<PlaylistItem>()
    val playlistItemLiveData: LiveData<PlaylistItem>
        get() = mutablePlaylistItemLiveData

    fun sendPlaylist(playlistItem: PlaylistItem) {
        mutablePlaylistItemLiveData.value = playlistItem
    }

    fun addPlaylist(playlistItem: PlaylistItem) {
        musicDatabaseRepositoryImpl.addPlaylist(playlistItem)
    }

    fun getListOfPlaylists() {
        disposable =
            musicDatabaseRepositoryImpl.getListOfAllPlaylists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { listOfPlaylists ->
                    mutableListOfPlaylistsLiveData.value = listOfPlaylists
                }
    }

    fun getSongsFromPlaylistById(playlistId: Long): Single<List<SongItem>> {
        return musicDatabaseRepositoryImpl.getSongsFromPlaylistByPlaylistId(playlistId)
    }

    fun clearAndAddNewSongsToPlaylist(playlistId: Long, list: List<SongItem>) {
        musicDatabaseRepositoryImpl.clearAndAddNewSongsToPlaylist(playlistId, list)
    }

    fun deleteFromPlaylist(playlistId: Long, songId: Long) {
        musicDatabaseRepositoryImpl.deleteFromPlaylist(playlistId, songId)
    }

    fun deletePlaylist(playlistId: Long) {
        musicDatabaseRepositoryImpl.deletePlaylist(playlistId)
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}