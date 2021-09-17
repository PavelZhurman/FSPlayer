package com.github.pavelzhurman.core


object ProjectConstants{
    const val DOWNLOAD_STATUS_ACTION = "com.github.pavelzhurman.DownloadStatusAction"
    const val SEND_DATA_TO_MINI_PLAYER_ACTION = "com.github.pavelzhurman.PlayerBroadcastReceiverData"
    const val SEND_CURRENT_POSITION_ACTION = "com.github.pavelzhurman.PlayerBroadcastReceiverPosition"

//PlayerCurrentSong
    const val CURRENT_POSITION_TAG = "com.github.pavelzhurman.current_position"
    const val URI_TAG = "com.github.pavelzhurman.uri"
    const val ARTIST_TAG = "com.github.pavelzhurman.artist"
    const val TITLE_TAG = "com.github.pavelzhurman.title"
    const val ALBUM_URI_TAG = "com.github.pavelzhurman.albumUri"
    const val DURATION_TAG = "com.github.pavelzhurman.duration"

//DownloadStatus
    const val DOWNLOAD_STATUS_TAG = "com.github.pavelzhurman.DownloadStatus"
    const val DOWNLOAD_STATUS_REASON = "com.github.pavelzhurman.DownloadStatusReason"
    const val DOWNLOAD_STATUS_PROGRESS = "com.github.pavelzhurman.DownloadStatusProgress"

    const val MAIN_PLAYLIST_ID = 0L
    const val FAVOURITE_PLAYLIST_ID = 1L
    const val ADD_PLAYLIST_ID = 2L

    const val NOT_DOWNLOADED_SONG_ID = -12L


    const val MAIN_PLAYLIST_NAME = "MainPlaylist"
    const val FAVOURITE_PLAYLIST_NAME = "FavouritePlaylist"
}
