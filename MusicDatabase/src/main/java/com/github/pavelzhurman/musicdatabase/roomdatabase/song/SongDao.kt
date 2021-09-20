package com.github.pavelzhurman.musicdatabase.roomdatabase.song

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSong(songItem: SongItem)

    @Query("SELECT * FROM songs WHERE songId =:id")
    fun getSongById(id: Long): SongItem

    @Query("DELETE FROM songs")
    fun clearAllSongs() : Completable
}