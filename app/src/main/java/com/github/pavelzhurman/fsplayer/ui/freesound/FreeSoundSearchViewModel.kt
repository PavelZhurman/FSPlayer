package com.github.pavelzhurman.fsplayer.ui.freesound


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.freesound_api.datasource.FreesoundRepositoryImpl
import com.github.pavelzhurman.freesound_api.datasource.downloader.DownloadManagerForFreesoundSongItems
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

    private val mutableFreesoundSongItemNextPagesListLiveData =
        MutableLiveData<FreesoundSongItem>()
    val freesoundSongItemListNextPagesLiveData: LiveData<FreesoundSongItem> =
        mutableFreesoundSongItemNextPagesListLiveData

    private val freesoundSongItemMutableLiveData = MutableLiveData<FreesoundSongItem>()
    val freesoundSongItemLiveData: LiveData<FreesoundSongItem>
        get() = freesoundSongItemMutableLiveData

    private val countMutableLiveData = MutableLiveData<Int>()
    val countLiveData: LiveData<Int>
        get() = countMutableLiveData

    private val nextMutableLiveData = MutableLiveData<Int?>()
    val nextLiveData: LiveData<Int?>
        get() = nextMutableLiveData


    private var disposable: Disposable? = null

    fun setFreesoundSongItem(freesoundSongItem: FreesoundSongItem) {
        freesoundSongItemMutableLiveData.value = freesoundSongItem
    }

    fun fetchFreesoundSearchData(query: String, page: Int) {
        disposable = freesoundRepositoryImpl.getFreesoundSearchData(query, page)

            .subscribeOn(Schedulers.io())
            .subscribe(
                { freesoundSearchData ->
                    countMutableLiveData.postValue(freesoundSearchData.count)
                    if (freesoundSearchData.next == null) {
                        nextMutableLiveData.postValue(null)

                    } else {
                        val pageIndex =
                            freesoundSearchData?.next?.split("page=")
                        Logger().logcatD("SplitterCheckTag", page.toString())
                        nextMutableLiveData.postValue(pageIndex?.get(1)?.toInt())
                    }

                    for (result in freesoundSearchData.results) {
                        freesoundRepositoryImpl.getSongInfo(result.id.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { freesoundSongItem ->

                                    mutableFreesoundSongItemNextPagesListLiveData.value =
                                        freesoundSongItem

                                },
                                { error ->
                                    Logger().logcatD("FREESOUND_API", error.message.toString())
                                })

                    }

                },
                { error -> Logger().logcatD("FREESOUND_API", error.message.toString()) }
            )
    }

    fun fetchFreesoundSearchData(query: String) {
        val freesoundSongItemList = mutableListOf<FreesoundSongItem>()
        disposable = freesoundRepositoryImpl.getFreesoundSearchData(query)

            .subscribeOn(Schedulers.io())
            .subscribe(
                { freesoundSearchData ->
                    countMutableLiveData.postValue(freesoundSearchData.count)
                    if (freesoundSearchData.next == null) {
                        nextMutableLiveData.postValue(null)

                    } else {
                        val pageIndex =
                            freesoundSearchData?.next?.split("page=")
                        Logger().logcatD("SplitterCheckTag", pageIndex.toString())
                        nextMutableLiveData.postValue(pageIndex?.get(1)?.toInt())
                    }

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