package com.github.pavelzhurman.core

import android.util.Log

class Logger {
    fun logcatD(key: String, message: String) {
        Log.d(key, message)
    }

    fun logcatE(key: String, message: String) {
        Log.e(key, message)
    }
}