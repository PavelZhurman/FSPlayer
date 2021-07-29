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

data class Previews(
    @SerializedName("preview-lq-ogg") val preview_lq_ogg: String,
    @SerializedName("preview-lq-mp3") val preview_lq_mp3: String,
    @SerializedName("preview-hq-ogg") val preview_hq_ogg: String,
    @SerializedName("preview-hq-mp3") val preview_hq_mp3: String
)

data class Images(
    val spectral_m: String,
    val spectral_l: String,
    val spectral_bw_l: String,
    val waveform_bw_m: String,
    val waveform_bw_l: String,
    val waveform_l: String,
    val waveform_m: String,
    val spectral_bw_m: String
)