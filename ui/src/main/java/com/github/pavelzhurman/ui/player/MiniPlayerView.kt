package com.github.pavelzhurman.ui.player

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.ui.R
import com.github.pavelzhurman.ui.databinding.ViewMiniPlayerBinding


class MiniPlayerView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var binding: ViewMiniPlayerBinding? = null

    private val drawableImagePause =
        AppCompatResources.getDrawable(context, R.drawable.ic_baseline_pause_24)
    private val drawableImagePlay =
        AppCompatResources.getDrawable(context, R.drawable.ic_baseline_play_arrow_24)

    init {
        inflate(context, R.layout.view_mini_player, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewMiniPlayerBinding.bind(this)
        binding?.textViewSongName?.isSelected = true
        binding?.textViewArtist?.isSelected = true
    }

    fun changeDrawableToPause() {
        binding?.apply {
            ImageLoader().loadDrawable(imageButtonPlay, drawableImagePause, imageButtonPlay)
        }
    }

    fun changeDrawableToPlay() {
        binding?.apply {
            ImageLoader().loadDrawable(imageButtonPlay, drawableImagePlay, imageButtonPlay)
        }
    }

    fun getBinding() = binding

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding = null
    }
}