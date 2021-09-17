package com.github.pavelzhurman.musicdatabase.roomdatabase.listened

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listened")
data class Listened(
    @field:PrimaryKey
    val postId: Long,
    val startPosition: Long,
    val total: Long
)