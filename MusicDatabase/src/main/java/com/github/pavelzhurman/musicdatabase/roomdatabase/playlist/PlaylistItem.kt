package com.github.pavelzhurman.musicdatabase.roomdatabase.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistItem(
    @PrimaryKey val playlistId: Long,
    val name: String,
    var currentPlaylist: Boolean
)