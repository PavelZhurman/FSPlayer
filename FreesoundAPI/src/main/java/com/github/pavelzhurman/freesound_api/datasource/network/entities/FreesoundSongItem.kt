package com.github.pavelzhurman.freesound_api.datasource.network.entities

import com.google.gson.annotations.SerializedName

data class FreesoundSongItem(
    val id: String,
    val name: String,
    val description: String,
    val tags: List<String>,
    val fileSize: Int,
    val duration: Double,
    val username: String,
    val download: String,
    val previews: Previews,
    val images: Images,
    val num_downloads: Int
)

