package com.github.pavelzhurman.fsplayer

import android.app.Application
import com.github.pavelzhurman.fsplayer.di.DiProvider
import com.github.pavelzhurman.fsplayer.di.SubComponents


class App : Application(), SubComponents {

    override fun onCreate() {
        super.onCreate()
        DiProvider.buildDi(this)
    }
}