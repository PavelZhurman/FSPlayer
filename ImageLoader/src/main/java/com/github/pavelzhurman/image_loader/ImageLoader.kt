package com.github.pavelzhurman.image_loader

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.pavelzhurman.core.Logger

class ImageLoader {

    fun loadPoster(activity: Activity, url: String, imageView: ImageView) {
        Glide.with(activity).load(url).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.logRootCauses("TAGTAG")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Logger().logcatD("TAGTAG", "resourceReady")
                    imageView.setImageDrawable(resource)
                    return true
                    // TODO: 7/5/2021
                }
            }).into(imageView)
    }

    fun loadPoster(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.logRootCauses("TAGTAG")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Logger().logcatD("TAGTAG", "resourceReady")
                    imageView.setImageDrawable(resource)
                    return true
                    // TODO: 7/5/2021
                }
            }).into(imageView)
    }

    fun loadDrawable(view: ImageView, drawable: Drawable?, imageView: ImageView) {
        Glide.with(view).load(drawable).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
            .into(imageView)
    }

    fun loadBitmap(view: ImageView, bitmap: Bitmap, imageView: ImageView) {

        Glide.with(view).load(bitmap).placeholder(R.drawable.ic_baseline_play_circle_filled_24)
            .into(imageView)
    }



}