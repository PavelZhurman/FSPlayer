package com.github.pavelzhurman.fsplayer.di

import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponent
import com.github.pavelzhurman.freesound_api.datasource.network.di.FreesoundApiComponent
import com.github.pavelzhurman.freesound_api.datasource.network.di.RetrofitModule
import com.github.pavelzhurman.fsplayer.di.main.MainComponent
import com.github.pavelzhurman.musicdatabase.di.DatabaseComponent
import com.github.pavelzhurman.musicdatabase.di.DatabaseModule
import dagger.Module

@Module(
    subcomponents = [MainComponent::class, DatabaseComponent::class, ExoPlayerComponent::class, FreesoundApiComponent::class],
    includes = [
        DatabaseModule::class,
        RetrofitModule::class]
)
class AppModule