package com.github.pavelzhurman.fsplayer.ui.player

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.github.pavelzhurman.core.base.BaseActivity
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.ActivityPlayerBinding

class PlayerActivity : BaseActivity<ActivityPlayerBinding>() {

    override fun initViews() {
        initToolbar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_player))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun getViewBinding() = ActivityPlayerBinding.inflate(layoutInflater)

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, PlayerActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }

    }
}