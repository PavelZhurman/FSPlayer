package com.github.pavelzhurman.musicdatabase.di

import com.github.pavelzhurman.musicdatabase.contentprovider.CollectAudio
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {


    @Provides
    fun provideCollectAudio(): CollectAudio =
        CollectAudio()


}

