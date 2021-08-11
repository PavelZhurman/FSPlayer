package com.github.pavelzhurman.fsplayer.ui.player

import android.os.Bundle
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.databinding.FragmentPlayerBinding

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun initViews() {}

    override fun getViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)
}