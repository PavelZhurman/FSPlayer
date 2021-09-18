package com.github.pavelzhurman.freesound_api.datasource.downloader

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.github.pavelzhurman.core.Logger
import com.github.pavelzhurman.core.ProjectConstants.DOWNLOAD_STATUS_ACTION
import com.github.pavelzhurman.core.ProjectConstants.DOWNLOAD_STATUS_PROGRESS
import com.github.pavelzhurman.core.ProjectConstants.DOWNLOAD_STATUS_REASON
import com.github.pavelzhurman.core.ProjectConstants.DOWNLOAD_STATUS_TAG
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

        val downloadId = downloadManager?.enqueue(request)

    }

/*    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.let {
            val query = DownloadManager.Query().setFilterById(downloadId)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val reason =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val progress = bytesDownloaded * 100f / bytesTotal
                Logger().logcatD("DownloadStatusCheckTAG", "progress $progress")
                Logger().logcatD("DownloadStatusCheckTAG", "bytesDownloaded $bytesDownloaded")
                Logger().logcatD("DownloadStatusCheckTAG", "bytesTotal $bytesTotal")


                cursor.close()

                val intentForBroadcastReceiver = Intent(DOWNLOAD_STATUS_ACTION).apply {
                    putExtra(
                        DOWNLOAD_STATUS_TAG,
                        status
                    )
                    putExtra(
                        DOWNLOAD_STATUS_REASON,
                        reason
                    )
                    putExtra(
                        DOWNLOAD_STATUS_PROGRESS,
                        progress
                    )
                }

                context.sendBroadcast(intentForBroadcastReceiver)

                Logger().logcatD("DownloadStatusCheckTAG", status.toString())

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
    }*/

}
