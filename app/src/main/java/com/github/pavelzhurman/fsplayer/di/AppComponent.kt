package com.github.pavelzhurman.fsplayer.di

import android.content.Context
import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponent
import com.github.pavelzhurman.freesound_api.datasource.network.di.FreesoundApiComponent
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.musicdatabase.di.DatabaseComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    val mainComponent: MainComponent.Factory
    val exoPlayerComponent: ExoPlayerComponent.Factory
    val databaseComponent: DatabaseComponent.Builder
    val freesoundApiComponent: FreesoundApiComponent.Factory
}