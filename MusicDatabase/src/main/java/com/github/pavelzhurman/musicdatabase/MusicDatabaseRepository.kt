package com.github.pavelzhurman.musicdatabase

import android.content.Context
import androidx.room.Transaction
import com.github.pavelzhurman.core.ProjectConstants.FAVOURITE_PLAYLIST_ID
import com.github.pavelzhurman.core.ProjectConstants.FAVOURITE_PLAYLIST_NAME
import com.github.pavelzhurman.core.ProjectConstants.MAIN_PLAYLIST_ID
import com.github.pavelzhurman.core.ProjectConstants.MAIN_PLAYLIST_NAME
import com.github.pavelzhurman.musicdatabase.contentprovider.CollectAudio
import com.github.pavelzhurman.musicdatabase.roomdatabase.MusicDatabase
import com.github.pavelzhurman.musicdatabase.roomdatabase.PlaylistSongCrossRef
import com.github.pavelzhurman.musicdatabase.roomdatabase.listened.Listened
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton


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
    fun updateSongsInDatabase() {
        clearAllSongsFromDB().subscribe {
            musicDatabase.getMusicDAO().deleteAllSongsFromPlaylist(MAIN_PLAYLIST_ID)
            collectAudioAndAddToMainPlaylist()

        }
/*        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getSongItemDAO().clearAllSongs()
            musicDatabase.getPlaylistItemDAO().updatePlaylist(
                PlaylistItem(
                    MAIN_PLAYLIST_ID,
                    MAIN_PLAYLIST_NAME, true
                )
            )

        }*/

    }

    @Transaction
    fun addPlaylist(playlistItem: PlaylistItem) {

        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            Single.create<Long> { emitter ->
                var tempId = playlistItem.playlistId
                while (musicDatabase.getPlaylistItemDAO().isExists(tempId)) {
                    tempId++
                }
                emitter.onSuccess(tempId)
            }.subscribeOn(Schedulers.io()).subscribe { tempId ->

                Completable.complete().subscribe {
                    musicDatabase.getPlaylistItemDAO().addPlaylist(
                        PlaylistItem(
                            tempId,
                            playlistItem.name,
                            playlistItem.currentPlaylist
                        )
                    )
                }
            }

        }
    }

    fun insertListened(listened: Listened) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getListenedDAO().insert(listened)
        }
    }

    fun getCurrentPlaylist(): Maybe<PlaylistItem> =
        musicDatabase.getPlaylistItemDAO().getCurrentPlaylist().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    @Transaction
    fun updateCurrentPlaylistState(playlistItem: PlaylistItem) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            with(musicDatabase.getPlaylistItemDAO()) {
                setFalseForCurrentPlaylist()
                updatePlaylist(playlistItem)
            }

        }
    }

    @Transaction
    fun addSongToDatabaseAndMainPlaylist(songItem: SongItem) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getSongItemDAO().addSong(songItem)
            if (musicDatabase.getPlaylistItemDAO().isExists(MAIN_PLAYLIST_ID)) {
                musicDatabase.getMusicDAO().addSongToPlaylist(MAIN_PLAYLIST_ID, songItem.songId)
            } else {
                initMainPlaylist()
                musicDatabase.getMusicDAO().addSongToPlaylist(MAIN_PLAYLIST_ID, songItem.songId)
            }
        }

    }

    fun clearAllSongsFromDB(): Completable {
        return musicDatabase.getSongItemDAO().clearAllSongs().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    @Transaction
    fun collectAudioAndAddToMainPlaylist() {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {

            val list = CollectAudio().collectAudio(context)
            if (list.isNotEmpty()) {
                initMainPlaylist()
                list.forEach { songItem ->
                    addSongsToMainPlaylist(songItem)
                }
            }

        }
    }

    private fun initFavouritePlaylist() {
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

        val playlistSongCrossRef = PlaylistSongCrossRef(MAIN_PLAYLIST_ID, songItem.songId)
        with(musicDatabase) {
            getMusicDAO().addSongsToPlaylistAddDatabase(playlistSongCrossRef)
            getSongItemDAO().addSong(songItem)
        }

    }

    private fun initMainPlaylist() {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {

            with(musicDatabase.getPlaylistItemDAO()) {
                if (!isExists(MAIN_PLAYLIST_ID)) {
                    addPlaylist(
                        PlaylistItem(
                            MAIN_PLAYLIST_ID,
                            MAIN_PLAYLIST_NAME,
                            currentPlaylist = true
                        )
                    )
                }
            }

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