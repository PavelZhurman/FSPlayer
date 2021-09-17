package com.github.pavelzhurman.fsplayer.ui.freesound

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.core.ProjectConstants
import com.github.pavelzhurman.freesound_api.datasource.FreesoundRepositoryImpl
import com.github.pavelzhurman.freesound_api.datasource.downloader.DownloadManagerForFreesoundSongItems
import com.github.pavelzhurman.freesound_api.datasource.downloader.DownloadStatus
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FreeSoundSearchViewModel
@Inject constructor(
    private val freesoundRepositoryImpl: FreesoundRepositoryImpl,
) :
    ViewModel() {

    @Inject
    lateinit var downloadManagerForFreesoundSongItems: DownloadManagerForFreesoundSongItems

    private val mutableFreesoundSongItemListLiveData = MutableLiveData<List<FreesoundSongItem>>()
    val freesoundSongItemListLiveData: LiveData<List<FreesoundSongItem>> =
        mutableFreesoundSongItemListLiveData

    private val downloadStatusMutableLiveData: MutableLiveData<DownloadStatus> = MutableLiveData()
    val downloadStatusLiveData: LiveData<DownloadStatus>
        get() = downloadStatusMutableLiveData

    private val downloadedProgressMutableLiveData = MutableLiveData<Float?>()
    val downloadedProgressLiveData: MutableLiveData<Float?>
        get() = downloadedProgressMutableLiveData

    val freesoundSongItemMutableLiveData = MutableLiveData<FreesoundSongItem>()

    private var disposable: Disposable? = null

    fun fetchFreesoundSearchData(query: String) {
        val freesoundSongItemList = mutableListOf<FreesoundSongItem>()
        disposable = freesoundRepositoryImpl.getFreesoundSearchData(query)

            .subscribeOn(Schedulers.io())
            .subscribe(
                { freesoundSearchData ->
                    for (result in freesoundSearchData.results) {
                        freesoundRepositoryImpl.getSongInfo(result.id.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { freesoundSongItem ->
                                    freesoundSongItemList.add(freesoundSongItem)
                                    mutableFreesoundSongItemListLiveData.value =
                                        freesoundSongItemList
                                },
                                { error ->
                                    Logger().logcatD("FREESOUND_API", error.message.toString())
                                })
                    }
                },
                { error -> Logger().logcatD("FREESOUND_API", error.message.toString()) }
            )
    }

    fun downloadSong(freesoundSongItem: FreesoundSongItem) {
        freesoundRepositoryImpl.downloadFreesoundSongItem(freesoundSongItem)
    }


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}