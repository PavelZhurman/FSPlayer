package com.github.pavelzhurman.musicdatabase

import android.content.Context
import androidx.room.Transaction
import com.github.pavelzhurman.musicdatabase.contentprovider.CollectAudio
import com.github.pavelzhurman.musicdatabase.roomdatabase.MusicDatabase
import com.github.pavelzhurman.musicdatabase.roomdatabase.PlaylistSongCrossRef
import com.github.pavelzhurman.musicdatabase.roomdatabase.listened.Listened
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

const val MAIN_PLAYLIST_ID = 0L
const val FAVOURITE_PLAYLIST_ID = 1L

const val MAIN_PLAYLIST_NAME = "MainPlaylist"
const val FAVOURITE_PLAYLIST_NAME = "FavouritePlaylist"

@Singleton
class MusicDatabaseRepository(private val context: Context) {


    private val musicDatabase: MusicDatabase = MusicDatabase.init(context)


    @Transaction
    fun clearAndAddNewSongsToPlaylist(playlistId: Long, list: List<SongItem>) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            with(musicDatabase.getMusicDAO()) {
                deleteAllSongsFromPlaylist(playlistId)
                list.forEach { songItem -> addSongToPlaylist(playlistId, songItem.songId) }
            }

        }
    }

    @Transaction
    fun addPlaylist(playlistItem: PlaylistItem) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            var tempId = playlistItem.playlistId
            while (musicDatabase.getPlaylistItemDAO().isExists(tempId)) {
                tempId++
            }
            musicDatabase.getPlaylistItemDAO().addPlaylist(
                PlaylistItem(
                    tempId,
                    playlistItem.name,
                    playlistItem.currentPlaylist
                )
            )
        }

    }

    fun getCurrentPlaylist(): Single<PlaylistItem>? {
        return Single.create<PlaylistItem> {
            it.onSuccess(musicDatabase.getPlaylistItemDAO().getCurrentPlaylist())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun updateSong(songItem: SongItem) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getSongItemDAO().updateSong(songItem)
        }
    }

    fun updateCurrentPlaylistState(playlistItem: PlaylistItem) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getPlaylistItemDAO().setFalseForCurrentPlaylist()
            musicDatabase.getPlaylistItemDAO().updatePlaylist(playlistItem)
        }
    }

    @Transaction
    fun collectAudioAndAddToMainPlaylist() {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {

            val list = CollectAudio().collectAudio(context)
            list.forEach { songItem ->
                addSongsToMainPlaylist(songItem)
            }
        }
    }

    fun initFavouritePlaylist() {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            var playlistItem: PlaylistItem? = musicDatabase.getPlaylistItemDAO().getPlaylistById(
                FAVOURITE_PLAYLIST_ID
            )

            if (playlistItem == null) {
                playlistItem = PlaylistItem(FAVOURITE_PLAYLIST_ID, FAVOURITE_PLAYLIST_NAME, false)
                musicDatabase.getPlaylistItemDAO().addPlaylist(playlistItem)
            }
        }
    }

    @Transaction
    fun addFavouriteSong(songId: Long) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            if (!musicDatabase.getPlaylistItemDAO().isExists(FAVOURITE_PLAYLIST_ID)) {
                initFavouritePlaylist()
            }
            musicDatabase.getMusicDAO().addSongToPlaylist(FAVOURITE_PLAYLIST_ID, songId)
        }
    }

    fun deleteFavouriteSong(songId: Long) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getMusicDAO().deleteFromPlaylist(FAVOURITE_PLAYLIST_ID, songId)
        }
    }

    fun getFavouriteSongs(): Single<List<SongItem>> {
        return Single.create<List<SongItem>> {
            val list = mutableListOf<SongItem>()
            musicDatabase.getMusicDAO().getSongIdsFromPlaylist(FAVOURITE_PLAYLIST_ID)
                .forEach { id ->
                    list.add(musicDatabase.getSongItemDAO().getSongById(id))
                }
            it.onSuccess(list)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    @Transaction
    private fun addSongsToMainPlaylist(songItem: SongItem) {
        var playlistItem: PlaylistItem? = musicDatabase.getPlaylistItemDAO().getPlaylistById(0)

        if (playlistItem == null) {
            playlistItem =
                PlaylistItem(MAIN_PLAYLIST_ID, MAIN_PLAYLIST_NAME, currentPlaylist = true)
        }

        val playlistSongCrossRef = PlaylistSongCrossRef(playlistItem.playlistId, songItem.songId)
        with(musicDatabase) {
            getMusicDAO().addSongsToPlaylistAddDatabase(playlistSongCrossRef)
            getSongItemDAO().addSong(songItem)
            getPlaylistItemDAO().addPlaylist(playlistItem)
        }

    }

    fun getListOfAllPlaylists(): Single<List<PlaylistItem>> =
        musicDatabase.getPlaylistItemDAO().getAllPlaylists()

    @Transaction
    fun getSongsFromPlaylistByPlaylistId(id: Long): Single<List<SongItem>> {
        return Single.create<List<SongItem>> {
            val list = mutableListOf<SongItem>()
            musicDatabase.getMusicDAO().getSongIdsFromPlaylist(id).forEach { id ->
                list.add(musicDatabase.getSongItemDAO().getSongById(id))
            }
            it.onSuccess(list)

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    @Transaction
    fun getAllSongsFromMainPlaylist(): Single<List<SongItem>> {
        return Single.create<List<SongItem>> {
            val list = mutableListOf<SongItem>()
            musicDatabase.getMusicDAO().getSongIdsFromPlaylist(MAIN_PLAYLIST_ID).forEach { id ->
                list.add(musicDatabase.getSongItemDAO().getSongById(id))
            }
            it.onSuccess(list)

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    fun deleteFromPlaylist(playlistId: Long, songId: Long) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getMusicDAO().deleteFromPlaylist(playlistId, songId)
        }
    }

    @Transaction
    fun deletePlaylist(playlistId: Long) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            with(musicDatabase) {
                getMusicDAO().deleteAllSongsFromPlaylist(playlistId)
                getPlaylistItemDAO().deletePlaylist(playlistId)
            }
        }
    }

}