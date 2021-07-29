package com.github.pavelzhurman.freesound_api.datasource.network.entities

data class FreesoundSearchData(
    val count: Int,
    val next: String?,
    val results: List<Results>,
    val previous: String?
)

data class Results(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val username: String
)