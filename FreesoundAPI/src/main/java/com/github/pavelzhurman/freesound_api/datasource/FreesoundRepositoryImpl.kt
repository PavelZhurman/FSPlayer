package com.github.pavelzhurman.freesound_api.datasource

import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundAPIController
import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundDataSource
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single

class FreesoundRepositoryImpl : FreesoundRepository {
    private val freesoundDataSource: FreesoundDataSource = FreesoundAPIController()

    override fun getFreesoundSearchData(query: String): Single<FreesoundSearchData> =
        freesoundDataSource.getFreesoundSearchData(query)

    override fun getSongInfo(id: String): Single<FreesoundSongItem> =
        freesoundDataSource.getSongInfo(id)

}