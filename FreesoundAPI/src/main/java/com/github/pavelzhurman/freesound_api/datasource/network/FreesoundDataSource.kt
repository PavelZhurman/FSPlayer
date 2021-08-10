package com.github.pavelzhurman.freesound_api.datasource.network

import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FreesoundDataSource @Inject constructor(private val freesoundAPI: FreesoundAPI) {
    fun getFreesoundSearchData(query: String): Single<FreesoundSearchData> =
        freesoundAPI.getFreesoundSearchData(query)

    fun getSongInfo(id: String, fields: String): Single<FreesoundSongItem> =
        freesoundAPI.getSongInfo(id, fields)
}