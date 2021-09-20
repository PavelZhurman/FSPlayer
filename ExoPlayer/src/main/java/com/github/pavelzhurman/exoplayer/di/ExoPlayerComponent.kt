package com.github.pavelzhurman.exoplayer.di

import com.github.pavelzhurman.exoplayer.AudioPlayerService
import dagger.Subcomponent

@Subcomponent()
interface ExoPlayerComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ExoPlayerComponent
    }

    fun inject(audioPlayerService: AudioPlayerService)
}
