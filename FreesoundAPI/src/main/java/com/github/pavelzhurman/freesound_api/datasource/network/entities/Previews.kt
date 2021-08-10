package com.github.pavelzhurman.freesound_api.datasource.network.entities

import com.google.gson.annotations.SerializedName

data class Previews(
    @SerializedName("preview-lq-ogg") val preview_lq_ogg: String,
    @SerializedName("preview-lq-mp3") val preview_lq_mp3: String,
    @SerializedName("preview-hq-ogg") val preview_hq_ogg: String,
    @SerializedName("preview-hq-mp3") val preview_hq_mp3: String
)