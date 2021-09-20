package com.github.pavelzhurman.freesound_api.datasource.network.di

import dagger.Subcomponent

@Subcomponent(modules = [RetrofitModule::class])
interface FreesoundApiComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): FreesoundApiComponent
    }

}