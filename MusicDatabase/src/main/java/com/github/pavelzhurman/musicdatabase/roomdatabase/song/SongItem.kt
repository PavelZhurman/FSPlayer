package com.github.pavelzhurman.musicdatabase.roomdatabase.song

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongItem(
    @PrimaryKey val songId: Long,
    val uri: String,
    val name: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val albumUri: String,
)