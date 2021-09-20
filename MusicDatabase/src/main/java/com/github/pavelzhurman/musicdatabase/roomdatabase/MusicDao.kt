package com.github.pavelzhurman.musicdatabase.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MusicDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addSongsToPlaylistAddDatabase(playlistSongCrossRef: PlaylistSongCrossRef)

    @Transaction
    @Query("SELECT songId FROM PlaylistSongCrossRef WHERE playlistId = :playlistId")
    fun getSongIdsFromPlaylist(playlistId: Long): List<Long>

    @Query("INSERT INTO PlaylistSongCrossRef (playlistId, songId) VALUES (:playlistId,:id)")
    fun addSongToPlaylist(playlistId: Long, id: Long)

    @Query("DELETE FROM PlaylistSongCrossRef WHERE playlistId = :playlistId AND songId = :songId")
    fun deleteFromPlaylist(playlistId: Long, songId: Long)

    @Query("DELETE FROM PlaylistSongCrossRef WHERE playlistId = :playlistId")
    fun deleteAllSongsFromPlaylist(playlistId: Long)
}