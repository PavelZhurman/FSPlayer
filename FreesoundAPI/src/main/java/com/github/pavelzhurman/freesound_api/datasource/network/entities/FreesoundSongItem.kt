package com.github.pavelzhurman.freesound_api.datasource.network.entities

data class FreesoundSongItem(
    val id: String,
    val name: String,
    val description: String,
    val tags: List<String>,
    val filesize: Int,
    val duration: Double,
    val username: String,
    val download: String,
    val previews: Previews,
    val images: Images,
    val num_downloads: Int
)

