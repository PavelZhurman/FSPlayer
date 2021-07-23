package com.github.pavelzhurman.ui.player

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.pavelzhurman.image_loader.ImageLoader
import com.github.pavelzhurman.ui.R
import com.github.pavelzhurman.ui.databinding.ViewMiniPlayerBinding


class MiniPlayerView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var binding: ViewMiniPlayerBinding? = null
    private val drawableImagePause =
        AppCompatResources.getDrawable(context, R.drawable.ic_baseline_pause_24)

    init {
        inflate(context, R.layout.view_mini_player, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewMiniPlayerBinding.bind(this)

        /*
        binding биндится только после этого метода,
        поэтому иниты лисенеров пришлось сделать здесь
        */
        initImageButtonSetOnClickListener()
        initSeekBarListener()
    }

    private fun initImageButtonSetOnClickListener() {
        binding?.apply {

            imageButtonPlay.setOnClickListener {
                seekBar.incrementProgressBy(1)
                ImageLoader().loadDrawable(imageButtonPlay, drawableImagePause, imageButtonPlay)
//                imageButtonPlay.setImageDrawable(drawable)
            }
        }
    }

    private fun initSeekBarListener() {
        binding?.apply {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar != null) {
                        Toast.makeText(context, seekBar.progress.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }

    }

    fun getBinding() = binding

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding = null
    }
}