package com.github.pavelzhurman.fsplayer.ui.freesound

import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.databinding.FragmentFreeSoundAuthorizationBinding
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class FreeSoundAuthorizationFragment :
    BaseFragment<FragmentFreeSoundAuthorizationBinding, FreeSoundAuthorizationViewModel>() {

    override fun initObservers(viewModel: FreeSoundAuthorizationViewModel) {}

    override fun initViews() {
        with(binding) {
            player.setOnClickListener { PlayerActivity.start(requireContext()) }
            buttonLogin.setOnClickListener { openFreeSoundSearchFragment() }
        }
    }

    private fun openFreeSoundSearchFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_freeSoundAuthorizationFragment_to_freeSoundSearchFragment)
    }

    override val viewModelClass: Class<FreeSoundAuthorizationViewModel> =
        FreeSoundAuthorizationViewModel::class.java

    override fun getViewBinding() = FragmentFreeSoundAuthorizationBinding.inflate(layoutInflater)
}