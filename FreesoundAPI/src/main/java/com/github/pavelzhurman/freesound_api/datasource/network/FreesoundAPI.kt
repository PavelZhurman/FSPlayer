package com.github.pavelzhurman.freesound_api.datasource.network

import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FreesoundAPI {

    @GET("apiv2/search/text/")
    fun getFreesoundSearchData(@Query("query") query: String): Single<FreesoundSearchData>

    @GET("apiv2/sounds/{sound_id}/")
    fun getSongInfo(
        @Path("sound_id") id: String,
        @Query("fields", encoded = true) fields: String,
    ): Single<FreesoundSongItem>

}