package com.github.pavelzhurman.freesound_api.datasource.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import javax.inject.Inject


class DownloadManagerForFreesoundSongItems @Inject constructor(private val context: Context) {

    fun downloadFreesoundSongItem(fileName: String, url: String, notificationTitle: String): Long? {

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(notificationTitle)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setVisibleInDownloadsUi(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager

        Log.d("DOWNLOADCHECK_TAG", "downl")
        val id = downloadManager?.enqueue(request)
        if (id != null) {
            getDownloadStatus(id)
        }
        return id
    }

    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.let {
            val query = DownloadManager.Query().setFilterById(downloadId)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val reason = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val progress = bytesDownloaded * 100f / bytesTotal
                Log.v("DownloadStatusCheckTAG", "progress $progress")
                Log.v("DownloadStatusCheckTAG", "bytesDownloaded $bytesDownloaded")
                Log.v("DownloadStatusCheckTAG", "bytesTotal $bytesTotal")


                cursor.close()

                Log.v("DownloadStatusCheckTAG", status.toString())

                return when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Downloaded
                    DownloadManager.STATUS_FAILED -> DownloadStatus.Error(reason)
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.Downloading(progress)
                    DownloadManager.STATUS_PAUSED -> DownloadStatus.Downloading(progress)
                    DownloadManager.STATUS_PENDING -> DownloadStatus.Downloading(progress)

                    else -> DownloadStatus.Unknown
                }
            }


        }
        return DownloadStatus.Unknown
    }


}