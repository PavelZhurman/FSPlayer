package com.github.pavelzhurman.fsplayer.ui.freesound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class FreesoundSearchViewModelFactory<VM : ViewModel>
@Inject constructor(private val viewModelProvider: Provider<VM>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        viewModelProvider.get() as T
}