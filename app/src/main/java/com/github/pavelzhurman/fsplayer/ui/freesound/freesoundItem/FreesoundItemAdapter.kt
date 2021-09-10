package com.github.pavelzhurman.fsplayer.ui.freesound.freesoundItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemFreesoundSongItemBinding
import com.github.pavelzhurman.image_loader.ImageLoader

class FreesoundItemAdapter(private val list: List<String>) :
    RecyclerView.Adapter<FreesoundItemAdapter.FreesoundItemViewHolder>() {

    inner class FreesoundItemViewHolder(private val itemFreesoundSongItemBinding: ItemFreesoundSongItemBinding) :
        RecyclerView.ViewHolder(itemFreesoundSongItemBinding.root) {

        fun bind(image: String) {
            ImageLoader().loadPoster(
                itemFreesoundSongItemBinding.root.context,
                image,
                itemFreesoundSongItemBinding.imageView
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreesoundItemViewHolder =
        FreesoundItemViewHolder(
            ItemFreesoundSongItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FreesoundItemViewHolder, position: Int) {
        holder.bind(image = list[position])
    }

    override fun getItemCount(): Int = list.size
}