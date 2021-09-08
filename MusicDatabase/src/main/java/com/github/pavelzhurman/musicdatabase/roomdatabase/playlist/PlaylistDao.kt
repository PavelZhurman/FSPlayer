package com.github.pavelzhurman.musicdatabase.roomdatabase.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Single

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPlaylist(playlistItem: PlaylistItem): Long

    @Update
    fun updatePlaylist(playlistItem: PlaylistItem)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Single<List<PlaylistItem>>

    @Query("SELECT * FROM playlists WHERE currentPlaylist = 1")
    fun getCurrentPlaylist(): PlaylistItem

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun getPlaylistById(id: Long): PlaylistItem

    @Query("UPDATE playlists SET currentPlaylist = 0 WHERE currentPlaylist=1")
    fun setFalseForCurrentPlaylist()

}