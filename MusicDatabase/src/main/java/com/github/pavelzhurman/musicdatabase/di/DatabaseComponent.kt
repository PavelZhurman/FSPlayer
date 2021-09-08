package com.github.pavelzhurman.musicdatabase.di

import dagger.Subcomponent

@Subcomponent(modules = [DatabaseModule::class])
interface DatabaseComponent {

    @Subcomponent.Builder
    interface Builder {
        fun databaseModule(databaseModule: DatabaseModule): Builder
        fun build(): DatabaseComponent
    }

    fun inject(databaseModule: DatabaseModule)
}