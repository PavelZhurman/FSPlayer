package com.github.pavelzhurman.image_loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.pavelzhurman.core.Logger

class ImageLoader {

    fun loadPoster(context: Context?, url: String?, imageView: ImageView) {
        if (context != null) {
            Glide.with(context).load(url).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        e?.logRootCauses("LoadingPosterCheckTAG")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Logger().logcatD("LoadingPosterCheckTAG", "resourceReady")
                        imageView.setImageDrawable(resource)
                        return true
                        // TODO: 7/5/2021
                    }
                }).into(imageView)
        }
    }

    fun loadDrawable(view: ImageView, drawable: Drawable?, imageView: ImageView) {
        Glide.with(view).load(drawable).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
            .into(imageView)
    }

}