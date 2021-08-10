package com.github.pavelzhurman.fsplayer.ui.freesound

import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundAuthorizationBinding

class FreeSoundAuthorizationFragment :
    BaseFragment<FragmentFreeSoundAuthorizationBinding>() {

    override fun initViews() {
        with(binding) {
            buttonLogin.setOnClickListener { openFreeSoundSearchFragment() }
        }
    }

    private fun openFreeSoundSearchFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_freeSoundAuthorizationFragment_to_freeSoundSearchFragment)
    }

    override fun getViewBinding() = FragmentFreeSoundAuthorizationBinding.inflate(layoutInflater)
}