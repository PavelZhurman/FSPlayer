package com.github.pavelzhurman.musicdatabase.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistDao
import com.github.pavelzhurman.musicdatabase.roomdatabase.playlist.PlaylistItem
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongDao
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

@Database(
    entities = [SongItem::class,
        PlaylistItem::class,
        PlaylistSongCrossRef::class
    ],
    version = 1
)
abstract class MusicDatabase : RoomDatabase() {


    abstract fun getMusicDAO(): MusicDao
    abstract fun getPlaylistItemDAO(): PlaylistDao
    abstract fun getSongItemDAO(): SongDao

    companion object {
        fun init(context: Context) =
            Room.databaseBuilder(context, MusicDatabase::class.java, "music_database")
                .fallbackToDestructiveMigration()
                .build()

    }

}