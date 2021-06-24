package com.github.pavelzhurman.fsplayer.ui.player

import com.github.pavelzhurman.core.base.BaseActivity
import com.github.pavelzhurman.fsplayer.R

class PlayerActivity : BaseActivity<PlayerViewModel>() {
    override fun initObservers(viewModel: PlayerViewModel) {}

    override fun initViews() {
        setSupportActionBar(findViewById(R.id.toolbar_player))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val viewModelClass: Class<PlayerViewModel> = PlayerViewModel::class.java
    override val layout: Int = R.layout.activity_player
}