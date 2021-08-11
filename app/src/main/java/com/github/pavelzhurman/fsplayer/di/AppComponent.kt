package com.github.pavelzhurman.fsplayer.di

import com.github.pavelzhurman.freesound_api.datasource.network.di.RetrofitModule
import com.github.pavelzhurman.fsplayer.ui.freesound.search.FreeSoundSearchFragment
import com.github.pavelzhurman.fsplayer.ui.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RetrofitModule::class])
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(freeSoundSearchFragment: FreeSoundSearchFragment)
}