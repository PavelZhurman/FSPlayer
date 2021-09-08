package com.github.pavelzhurman.musicdatabase.roomdatabase.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addSong(songItem: SongItem)

    @Update
    fun updateSong(songItem: SongItem)

    @Query("SELECT * FROM songs WHERE songId =:id")
    fun getSongById(id: Long): SongItem

    @Query("DELETE FROM songs")
    fun clearAllSongs()
}