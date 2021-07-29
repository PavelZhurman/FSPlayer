package com.github.pavelzhurman.fsplayer.ui.freesound.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.freesound_api.datasource.FreesoundRepositoryImpl
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class FreeSoundSearchViewModel : ViewModel() {

    private val mutableFreesoundSongItemListLiveData = MutableLiveData<List<FreesoundSongItem>>()
    val freesoundSongItemListLiveData: LiveData<List<FreesoundSongItem>> =
        mutableFreesoundSongItemListLiveData


    fun fetchFreesoundSearchData(query: String) {
        val freesoundSongItemList = mutableListOf<FreesoundSongItem>()
        FreesoundRepositoryImpl().getFreesoundSearchData(query)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { freesoundSearchData ->
                    for (result in freesoundSearchData.results) {
                        FreesoundRepositoryImpl().getSongInfo(result.id.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { freesoundSongItem ->
                                freesoundSongItemList.add(freesoundSongItem)
                                mutableFreesoundSongItemListLiveData.value =
                                    freesoundSongItemList
                            }
                    }
                },
                { error -> Logger().logcatD("FREESOUND_API", error.message.toString()) }
            )
    }

}