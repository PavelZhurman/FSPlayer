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

@Singleton
class MusicDatabaseRepositoryImpl2(private val context: Context) {


    private val musicDatabase: MusicDatabase = MusicDatabase.init(context)


    fun addListenedSong(listened: Listened) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            musicDatabase.getListenedDAO().insert(listened)
        }
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
                playlistItem = PlaylistItem(FAVOURITE_PLAYLIST_ID, "FavouritePlaylist", false)
                musicDatabase.getPlaylistItemDAO().addPlaylist(playlistItem)
            }
        }
    }

    @Transaction
    fun addFavouriteSong(songId: Long) {
        Completable.complete().subscribeOn(Schedulers.io()).subscribe {
            if (musicDatabase.getPlaylistItemDAO().getPlaylistById(FAVOURITE_PLAYLIST_ID) == null){
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
            playlistItem = PlaylistItem(MAIN_PLAYLIST_ID, "MainPlaylist", currentPlaylist = true)
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

    fun getSongById(id: Long): Single<SongItem> {
        return Single.create<SongItem> {
            musicDatabase.getSongItemDAO().getSongById(id)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}