package com.github.pavelzhurman.fsplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.pavelzhurman.core.Logger


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger().logcatD("TagTag", localClassName)
        Logger().logcatE("TagTag", localClassName)

        val tvHelloWorld = findViewById<TextView>(R.id.tvHelloWorld)
        tvHelloWorld.text = getString(R.string.hello_world)
    }
}