package com.github.pavelzhurman.fsplayer.di

import android.content.Context
import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponent
import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponentProvider
import com.github.pavelzhurman.freesound_api.datasource.network.di.FreesoundApiComponent
import com.github.pavelzhurman.freesound_api.datasource.network.di.FreesoundApiComponentProvider
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.fsplayer.di.main.MainComponentProvider
import com.github.pavelzhurman.musicdatabase.di.DatabaseComponent
import com.github.pavelzhurman.musicdatabase.di.DatabaseComponentProvider
import com.github.pavelzhurman.musicdatabase.di.DatabaseModule

interface SubComponents :
    MainComponentProvider,
    DatabaseComponentProvider,
    ExoPlayerComponentProvider,
    FreesoundApiComponentProvider {

    override fun provideMainComponent(): MainComponent {
        return DiProvider.appComponent().mainComponent.create()
    }

    override fun provideExoPlayerComponent(): ExoPlayerComponent {
        return DiProvider.appComponent().exoPlayerComponent.create()
    }

    override fun provideDatabaseComponent(context: Context): DatabaseComponent {
        return DiProvider.appComponent().databaseComponent.databaseModule(DatabaseModule()).build()
    }

    override fun provideFreesoundApiComponent(): FreesoundApiComponent {
        return DiProvider.appComponent().freesoundApiComponent.create()
    }
}