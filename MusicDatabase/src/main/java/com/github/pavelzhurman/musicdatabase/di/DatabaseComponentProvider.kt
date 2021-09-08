package com.github.pavelzhurman.musicdatabase.di

import android.content.Context


interface DatabaseComponentProvider {
    fun provideDatabaseComponent(context: Context): DatabaseComponent
}