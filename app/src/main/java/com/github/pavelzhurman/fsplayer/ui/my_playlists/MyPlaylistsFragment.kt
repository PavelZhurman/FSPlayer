package com.github.pavelzhurman.fsplayer.ui.my_playlists

import android.content.Intent
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.core.base.BaseFragment
import com.github.pavelzhurman.fsplayer.R
import com.github.pavelzhurman.fsplayer.ui.player.PlayerActivity

class MyPlaylistsFragment : BaseFragment<MyPlaylistsViewModel>() {

    lateinit var buttonPlayer: Button
    lateinit var recyclerView: RecyclerView


    override fun initObservers(viewModel: MyPlaylistsViewModel) {}

    override fun initViews() {
        buttonPlayer = view?.findViewById(R.id.player) ?: Button(context)
        recyclerView =
            view?.findViewById(R.id.recycler_view_my_playlists) ?: RecyclerView(requireContext())

        buttonPlayer.setOnClickListener {
            val intentToStartPlayerActivity = Intent(context, PlayerActivity::class.java)
            startActivity(intentToStartPlayerActivity)
        }

    }

    override val viewModelClass: Class<MyPlaylistsViewModel> = MyPlaylistsViewModel::class.java
    override val layout: Int = R.layout.fragment_my_playlists
}