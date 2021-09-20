package com.github.pavelzhurman.freesound_api.datasource.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import javax.inject.Inject


class DownloadManagerForFreesoundSongItems @Inject constructor(private val context: Context) {

    fun downloadFreesoundSongItem(fileName: String, url: String, notificationTitle: String) {

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(notificationTitle)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setVisibleInDownloadsUi(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager

        downloadManager?.enqueue(request)

    }
}
