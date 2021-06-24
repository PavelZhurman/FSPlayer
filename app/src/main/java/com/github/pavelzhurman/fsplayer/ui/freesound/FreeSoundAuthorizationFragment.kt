package com.github.pavelzhurman.fsplayer.ui.freesound

import android.widget.Button
import androidx.navigation.Navigation
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R

class FreeSoundAuthorizationFragment : BaseFragment<FreeSoundAuthorizationViewModel>() {
    lateinit var bLogin: Button

    override fun initObservers(viewModel: FreeSoundAuthorizationViewModel) {}

    override fun initViews() {
        bLogin = requireView().findViewById(R.id.button_login)
        bLogin.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_freeSoundAuthorizationFragment_to_freeSoundSearchFragment)
        }
    }

    override val viewModelClass: Class<FreeSoundAuthorizationViewModel> =
        FreeSoundAuthorizationViewModel::class.java
    override val layout: Int = R.layout.fragment_free_sound_authorization
}