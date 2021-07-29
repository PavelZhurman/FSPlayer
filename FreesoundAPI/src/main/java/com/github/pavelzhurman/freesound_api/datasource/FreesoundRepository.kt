package com.github.pavelzhurman.freesound_api.datasource

import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single

interface FreesoundRepository {
    fun getFreesoundSearchData(query: String): Single<FreesoundSearchData>
    fun getSongInfo(id: String): Single<FreesoundSongItem>
}