package com.github.pavelzhurman.freesound_api.datasource

import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundDataSource
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Named

class FreesoundRepositoryImpl @Inject constructor(
    private val freesoundDataSource: FreesoundDataSource,
    @Named("fields") private val fields: String
) :
    FreesoundRepository {


    override fun getFreesoundSearchData(query: String): Single<FreesoundSearchData> =
        freesoundDataSource.getFreesoundSearchData(query)

    override fun getSongInfo(id: String): Single<FreesoundSongItem> =
        freesoundDataSource.getSongInfo(id, fields)

}