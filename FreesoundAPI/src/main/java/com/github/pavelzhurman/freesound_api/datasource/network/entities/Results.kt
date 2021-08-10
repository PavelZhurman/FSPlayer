package com.github.pavelzhurman.freesound_api.datasource.network.entities

data class Results(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val username: String
)