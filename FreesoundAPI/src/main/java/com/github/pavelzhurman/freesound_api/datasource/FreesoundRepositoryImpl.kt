package com.github.pavelzhurman.freesound_api.datasource

import com.github.pavelzhurman.freesound_api.datasource.downloader.DownloadManagerForFreesoundSongItems
import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundDataSource
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Named

class FreesoundRepositoryImpl @Inject constructor(
    private val freesoundDataSource: FreesoundDataSource,
    @Named("fields") private val fields: String,
    private val downloadManager: DownloadManagerForFreesoundSongItems
) :
    FreesoundRepository {

    override fun getFreesoundSearchData(query: String): Single<FreesoundSearchData> =
        freesoundDataSource.getFreesoundSearchData(query)

    override fun getSongInfo(id: String): Single<FreesoundSongItem> =
        freesoundDataSource.getSongInfo(id, fields)

    fun downloadFreesoundSongItem(freesoundSongItem: FreesoundSongItem) =
        downloadManager.downloadFreesoundSongItem(
            fileName = freesoundSongItem.name,
            url = freesoundSongItem.previews.preview_hq_mp3,
            notificationTitle = freesoundSongItem.name
        )

    fun getDownloadStatus(downloadId: Long) = downloadManager.getDownloadStatus(downloadId)
}