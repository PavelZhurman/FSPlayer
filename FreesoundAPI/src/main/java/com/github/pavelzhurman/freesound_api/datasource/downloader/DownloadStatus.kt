package com.github.pavelzhurman.freesound_api.datasource.downloader

sealed class DownloadStatus {
    object Unknown : DownloadStatus()
    data class Downloading(val progress: Float) : DownloadStatus()
    object Downloaded : DownloadStatus()
    data class Error(val reason: String? = null) : DownloadStatus()
}