package com.github.pavelzhurman.musicdatabase.roomdatabase.listened

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ListenedDao {

    @Query("SELECT * FROM listened WHERE postId = :id")
    fun getListenedById(id: String): Listened

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(listened: Listened)

    @Delete
    fun delete(listened: Listened)

    @Query("DELETE FROM listened")
    fun clearTable()
}