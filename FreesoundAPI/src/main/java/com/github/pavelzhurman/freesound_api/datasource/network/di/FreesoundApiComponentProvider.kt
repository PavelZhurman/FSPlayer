package com.github.pavelzhurman.freesound_api.datasource.network.di

interface FreesoundApiComponentProvider {
    fun provideFreesoundApiComponent(): FreesoundApiComponent
}