package com.github.pavelzhurman.fsplayer.di.main

import com.github.pavelzhurman.fsplayer.ui.freesound.FreeSoundSearchFragment
import com.github.pavelzhurman.fsplayer.ui.main.MainActivity
import com.github.pavelzhurman.fsplayer.ui.main.MainFragment
import com.github.pavelzhurman.fsplayer.ui.player.PlayerFragment
import dagger.Subcomponent

@Subcomponent
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(freeSoundSearchFragment: FreeSoundSearchFragment)
    fun inject(playerFragment: PlayerFragment)


}