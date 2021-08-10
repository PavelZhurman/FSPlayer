package com.github.pavelzhurman.freesound_api.datasource.network.entities

data class FreesoundSearchData(
    val count: Int,
    val next: String?,
    val results: List<Results>,
    val previous: String?
)