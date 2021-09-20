package com.github.pavelzhurman.fsplayer.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.github.pavelzhurman.fsplayer.databinding.ItemSearchFragmentBinding
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem

class SearchFragmentAdapter(private var list: List<SongItem>) :
    RecyclerView.Adapter<SearchFragmentAdapter.SearchFragmentViewHolder>(), Filterable {

    private var listWithAllValues = list

    lateinit var onItemClickListener: (position: Int) -> Unit

    inner class SearchFragmentViewHolder(private val itemSearchFragmentBinding: ItemSearchFragmentBinding) :
        RecyclerView.ViewHolder(itemSearchFragmentBinding.root) {

        fun bind(songItem: SongItem) {
            with(itemSearchFragmentBinding) {
                textViewArtist.text = songItem.artist
                textViewSongName.text = songItem.title
                ImageLoader().loadPoster(root.context, songItem.albumUri, imageViewPoster)

                root.setOnClickListener {

                    onItemClickListener.invoke(listWithAllValues.indexOf(songItem))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragmentViewHolder =
        SearchFragmentViewHolder(
            ItemSearchFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: SearchFragmentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val resultList = mutableListOf<SongItem>()

                if (constraint.isNullOrEmpty()) {
                    filterResults.values = listWithAllValues
                } else {
                    val enteredChars = constraint.toString().lowercase().trim()

                    listWithAllValues.forEach { songItem ->
                        val artist = songItem.artist.lowercase().trim()
                        val title = songItem.title.lowercase().trim()

                        if (artist.contains(enteredChars) || title.contains(enteredChars)) {
                            resultList.add(songItem)
                        }
                        filterResults.values = resultList
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list = results?.values as List<SongItem>
                notifyDataSetChanged()
            }

        }
    }
}