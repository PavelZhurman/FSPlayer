package com.github.pavelzhurman.fsplayer.ui.freesound

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import com.github.pavelzhurman.fsplayer.databinding.ItemFreesoundSearchSongsBinding
import com.github.pavelzhurman.image_loader.ImageLoader

class FreesoundSearchDataItemAdapter :
    RecyclerView.Adapter<FreesoundSearchDataItemAdapter.FreesoundSearchDataItemViewHolder>() {

    var values = emptyList<FreesoundSongItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var onPlayButtonClickListener: (freesoundSongItem: FreesoundSongItem) -> Unit
    lateinit var onDownloadClickListener: (freesoundSongItem: FreesoundSongItem) -> Unit

    inner class FreesoundSearchDataItemViewHolder(
        private val itemFreesoundSearchSongsBinding: ItemFreesoundSearchSongsBinding
    ) : RecyclerView.ViewHolder(itemFreesoundSearchSongsBinding.root) {
        fun bind(item: FreesoundSongItem) {
            val tags = item.tags.joinToString(separator = ",", prefix = "[", postfix = "]")

            with(itemFreesoundSearchSongsBinding) {
                textViewName.text = item.name
                textViewDescription.text = item.description
                textViewNumberOfDownloads.text = item.num_downloads.toString()
                textViewTags.text = tags
                textViewUsername.text = item.username
                ImageLoader().loadPoster(
                    itemFreesoundSearchSongsBinding.root.context,
                    item.images.waveform_m,
                    imageViewPoster
                )
                imageButtonPlay.setOnClickListener { onPlayButtonClickListener.invoke(item) }
                imageButtonDownload.setOnClickListener { onDownloadClickListener.invoke(item) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FreesoundSearchDataItemViewHolder =
        FreesoundSearchDataItemViewHolder(
            ItemFreesoundSearchSongsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FreesoundSearchDataItemViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size
}