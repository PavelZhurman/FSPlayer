package com.github.pavelzhurman.exoplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pavelzhurman.musicdatabase.roomdatabase.song.SongItem
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import android.provider.MediaStore
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import com.github.pavelzhurman.core.ProjectConstants.ALBUM_URI_TAG
import com.github.pavelzhurman.core.ProjectConstants.ARTIST_TAG
import com.github.pavelzhurman.core.ProjectConstants.CURRENT_POSITION_TAG
import com.github.pavelzhurman.core.ProjectConstants.DURATION_TAG
import com.github.pavelzhurman.core.ProjectConstants.SEND_CURRENT_POSITION_ACTION
import com.github.pavelzhurman.core.ProjectConstants.SEND_DATA_TO_MINI_PLAYER_ACTION
import com.github.pavelzhurman.core.ProjectConstants.TITLE_TAG
import com.github.pavelzhurman.core.ProjectConstants.URI_TAG
import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponent
import com.github.pavelzhurman.exoplayer.di.ExoPlayerComponentProvider
import com.github.pavelzhurman.musicdatabase.MusicDatabaseRepository
import com.github.pavelzhurman.musicdatabase.roomdatabase.listened.Listened
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_LOW
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray


private const val PLAYBACK_CHANNEL_ID = "playback_channel"
private const val PLAYBACK_NOTIFICATION_ID = 11
private const val MEDIA_SESSION_TAG = "sed_audio"




class AudioPlayerService : LifecycleService() {

    private var disposable: Disposable? = null

    private val musicDatabaseRepositoryImpl2: MusicDatabaseRepository by lazy {
        MusicDatabaseRepository(applicationContext)
    }

    lateinit var exoPlayerComponent: ExoPlayerComponent

    private val player: SimpleExoPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()


        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(audioAttributes, true)
        }
    }

    private val playerStatusMutableLiveData = MutableLiveData<PlayerStatus>()
    val playerStatusLiveData: LiveData<PlayerStatus>
        get() = playerStatusMutableLiveData

    private val currentIndexMutableLiveData = MutableLiveData<Int>()
    val currentIndexLiveData: LiveData<Int>
        get() = currentIndexMutableLiveData

    private val listOfSongsMutableLiveData = MutableLiveData<List<SongItem>>()
    val listOfSongsLiveData: LiveData<List<SongItem>>
        get() = listOfSongsMutableLiveData
    private var localListOfSongs = emptyList<SongItem>()


    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var playerNotificationManager: PlayerNotificationManager? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        observeCurrentPosition()
        return super.onStartCommand(intent, flags, startId)
    }

    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            playerStatusMutableLiveData.value = PlayerStatus.Cancelled()

            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing) {
                // Make sure the service will not get destroyed while playing media.
                startForeground(notificationId, notification)
            } else {
                // Make notification cancellable.
                stopForeground(false)
            }
        }
    }

    fun setSource(songItem: SongItem) {
        val list: List<SongItem> = mutableListOf(songItem)
        localListOfSongs = list
        listOfSongsMutableLiveData.value = list

        player.setMediaSource(createMediaSource(songItem.uri))
        player.prepare()
        player.play()
    }

    fun setSource(playlistId: Long?, songItem: SongItem?) {
        if (playlistId != null) {
            musicDatabaseRepositoryImpl2.getSongsFromPlaylistByPlaylistId(playlistId)
                .subscribe { listOfSongs ->
                    localListOfSongs = listOfSongs
                    listOfSongsMutableLiveData.value = listOfSongs
                    player.setMediaSource(createMediaSource(listOfSongs))
                    if (songItem != null) {
                        val index = listOfSongs.indexOf(songItem)
                        player.seekTo(index, 0L)
                        currentIndexMutableLiveData.value = index
                    }
                }
        }
    }

    private fun initApp() {
        exoPlayerComponent =
            (applicationContext as ExoPlayerComponentProvider).provideExoPlayerComponent()
        exoPlayerComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        initApp()

        musicDatabaseRepositoryImpl2.getCurrentPlaylist().subscribe { playlist ->
            musicDatabaseRepositoryImpl2.getSongsFromPlaylistByPlaylistId(playlist.playlistId)
                .subscribe { list ->
                    localListOfSongs = list
                    listOfSongsMutableLiveData.value = list

                    player.setMediaSource(createMediaSource())

                    playerNotificationManager = PlayerNotificationManager
                        .Builder(applicationContext, PLAYBACK_NOTIFICATION_ID, PLAYBACK_CHANNEL_ID)
                        .setChannelNameResourceId(R.string.channel_name_resource)
                        .setChannelImportance(IMPORTANCE_LOW)
                        .setNotificationListener(notificationListener)
                        .setMediaDescriptionAdapter(createMediaDescriptionAdapter())
                        .build().apply {

                            // Add stop action.
                            setUseStopAction(true)

                            setPlayer(player)
                        }

                    // Show lock screen controls and let apps like Google assistant manager playback.
                    mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_TAG).apply {
                        isActive = true
                    }
                    mediaSession?.sessionToken?.let {
                        playerNotificationManager?.setMediaSessionToken(
                            it
                        )
                    }
                    if (mediaSession != null) {
                        mediaSessionConnector = MediaSessionConnector(mediaSession!!).apply {
                            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                                override fun getMediaDescription(
                                    player: Player,
                                    windowIndex: Int
                                ): MediaDescriptionCompat {
                                    val bitmap =
                                        getBitmapFromUri(localListOfSongs[player.currentWindowIndex].albumUri)
                                            ?: getBitmapFromVectorDrawable(
                                                applicationContext,
                                                R.drawable.exo_ic_play_circle_filled
                                            )
                                    val extras = Bundle().apply {
                                        putParcelable(
                                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                            bitmap
                                        )
                                        putParcelable(
                                            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
                                            bitmap
                                        )
                                    }

                                    val title =
                                        localListOfSongs[player.currentWindowIndex].title ?: "..."
                                    val artist =
                                        localListOfSongs[player.currentWindowIndex].artist ?: "..."

                                    return MediaDescriptionCompat.Builder()
                                        .setIconBitmap(bitmap)
                                        .setTitle(artist)
                                        .setDescription(title)
                                        .setExtras(extras)
                                        .build()
                                }
                            })
                            setPlayer(player)
                        }
                    }
                }
        }
        player.addListener(PlayerEventListener())
        player.prepare()
        player.repeatMode = REPEAT_MODE_ALL
    }


    private fun createMediaDescriptionAdapter(): PlayerNotificationManager.MediaDescriptionAdapter =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return if (localListOfSongs.isNotEmpty()) {
                    localListOfSongs[player.currentWindowIndex]?.title
                        ?: getString(R.string.empty_string_dots)
                } else getString(R.string.empty_string_dots)
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, AudioPlayerService::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            override fun getCurrentContentText(player: Player): CharSequence {
                return if (localListOfSongs.isNotEmpty()) {
                    localListOfSongs[player.currentWindowIndex]?.title
                        ?: getString(R.string.empty_string_dots)
                } else {
                    getString(R.string.empty_string_dots)
                }
                /*почему-то при вызове artist приходит title*/
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? =
                if (localListOfSongs.isNotEmpty()) {
                    getBitmapFromUri(localListOfSongs[player.currentWindowIndex]?.albumUri)
                        ?: getBitmapFromVectorDrawable(
                            applicationContext,
                            R.drawable.exo_ic_play_circle_filled
                        )
                } else {
                    getBitmapFromVectorDrawable(
                        applicationContext,
                        R.drawable.exo_ic_play_circle_filled
                    )
                }

        }

    private fun getBitmapFromUri(uri: String): Bitmap? =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.createSource(applicationContext.contentResolver, uri?.toUri())
                    .let {
                        ImageDecoder.decodeBitmap(it)
                    }
            } else {
                MediaStore.Images.Media.getBitmap(
                    applicationContext.contentResolver,
                    uri?.toUri()
                )
            }
        } catch (e: FileNotFoundException) {
            getBitmapFromVectorDrawable(
                applicationContext,
                R.drawable.exo_ic_play_circle_filled
            )
        }

    private fun getBitmapFromVectorDrawable(
        context: Context,
        @DrawableRes drawableId: Int
    ): Bitmap? {
        return ContextCompat.getDrawable(context, drawableId)?.let {
            val drawable = DrawableCompat.wrap(it).mutate()

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap
        }
    }

    private fun createMediaSource(stringMp3: String): ConcatenatingMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext)

        val concatenatingMediaSource = ConcatenatingMediaSource()

        val mediaItem = MediaItem.fromUri(stringMp3.toUri())
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        concatenatingMediaSource.addMediaSource(mediaSource)

        return concatenatingMediaSource
    }

    private fun createMediaSource(listOfSongs: List<SongItem>): ConcatenatingMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext)

        val concatenatingMediaSource = ConcatenatingMediaSource()
        for (song in listOfSongs) {
            val mediaItem = MediaItem.fromUri(song.uri)
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    private fun createMediaSource(): ConcatenatingMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext)

        val concatenatingMediaSource = ConcatenatingMediaSource()
        if (localListOfSongs.isNotEmpty()) {
            for (song in localListOfSongs) {
                if (song != null){
                    val mediaItem = MediaItem.fromUri(song?.uri)
                    val mediaSource =
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem)
                    concatenatingMediaSource.addMediaSource(mediaSource)
                }
                            }
        }
        return concatenatingMediaSource
    }

    private fun observeCurrentPosition() {
        playerStatusLiveData.observe(this, { status ->
            when (status) {
                is PlayerStatus.Playing -> {
                    val playbackProgressObservable: Observable<Long> =
                        Observable.interval(1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread()).map {
                                player.currentPosition
                            }
                    disposable =
                        playbackProgressObservable.observeOn(AndroidSchedulers.mainThread())
                            .subscribe { currentPosition ->

                                val intentForCurrentPosition =
                                    Intent(SEND_CURRENT_POSITION_ACTION).apply {
                                        putExtra(
                                            CURRENT_POSITION_TAG,
                                            currentPosition
                                        )
                                    }
                                sendBroadcast(intentForCurrentPosition)
                            }
                }
                else -> {
                }
            }
        })
    }

    private fun sendDataBroadcastForMiniPlayer() {
        if (localListOfSongs.isNullOrEmpty()) {
            val intentForBroadcast = Intent(SEND_DATA_TO_MINI_PLAYER_ACTION).apply {
                putExtra(
                    ARTIST_TAG,
                    getString(R.string.empty_string_dots)
                )
                putExtra(
                    TITLE_TAG,
                    getString(R.string.empty_string_dots)
                )
            }
            sendBroadcast(intentForBroadcast)
        } else {
            val intentForBroadcast = Intent(SEND_DATA_TO_MINI_PLAYER_ACTION).apply {
                putExtra(
                    URI_TAG,
                    localListOfSongs[player.currentWindowIndex].uri
                )
                putExtra(
                    ARTIST_TAG,
                    localListOfSongs[player.currentWindowIndex].artist
                )
                putExtra(
                    TITLE_TAG,
                    localListOfSongs[player.currentWindowIndex].title
                )
                putExtra(
                    ALBUM_URI_TAG,
                    localListOfSongs[player.currentWindowIndex].albumUri
                )
                putExtra(
                    DURATION_TAG,
                    localListOfSongs[player.currentWindowIndex].duration
                )
            }
            sendBroadcast(intentForBroadcast)
        }
    }

    private inner class PlayerEventListener : Player.Listener {


        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, playbackState)

            if (playbackState == Player.STATE_IDLE) {
                sendDataBroadcastForMiniPlayer()
            }

            if (playbackState == Player.STATE_READY) {
                sendDataBroadcastForMiniPlayer()
                if (player.playWhenReady) {
                    sendDataBroadcastForMiniPlayer()
                    playerStatusMutableLiveData.value = PlayerStatus.Playing()

                } else {// Paused
                    sendDataBroadcastForMiniPlayer()
                    playerStatusMutableLiveData.value = PlayerStatus.Paused()

                }
            } else if (playbackState == Player.STATE_BUFFERING) {
                playerStatusMutableLiveData.value =
                    PlayerStatus.Buffering()
            } else {
                playerStatusMutableLiveData.value = PlayerStatus.Other()
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
            sendDataBroadcastForMiniPlayer()
            currentIndexMutableLiveData.value = player.currentWindowIndex
        }

        override fun onPlayerError(error: PlaybackException) {
            playerStatusMutableLiveData.value = PlayerStatus.Error()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        val service
            get() = this@AudioPlayerService
    }

    fun pause() {
        player.pause()
        player.stop()
    }

    fun resume() {
        player.prepare()
        player.play()
    }

    fun next() {
        player.seekToNextWindow()
    }

    fun seekTo(position: Int) {
        val positionToSeek = (position.toDouble() * player.duration.toDouble() / 100.0)
        player.seekTo(positionToSeek.toLong())
    }

    fun getCurrentWindowIndex(): Int =
        player.currentWindowIndex

    fun setCurrentWindowIndex(index: Int) {
        player.seekTo(index, 0)
    }

    fun getCurrentMediaItemDuration(): Long =
        player.duration

    fun getCurrentPosition(): Long =
        player.currentPosition


    fun manageRepeatMode(): Int {
        player.repeatMode = if (player.repeatMode == REPEAT_MODE_ALL) {
            REPEAT_MODE_ONE
        } else {
            REPEAT_MODE_ALL
        }
        return player.repeatMode
    }

    fun manageShuffleMode(): Boolean {
        player.shuffleModeEnabled = !player.shuffleModeEnabled
        return player.shuffleModeEnabled
    }

    fun isPlayerPlaying(): Boolean = player.isPlaying

    fun goToPreviousOrBeginning() {
        player.run {
            if (hasPreviousWindow()) {
                seekToPreviousWindow()
            } else {
                seekToDefaultPosition()
            }
        }
    }

    override fun onDestroy() {

        val songItem: SongItem? = currentIndexLiveData.value?.let { index ->
            listOfSongsMutableLiveData.value?.get(index)
        }
        songItem?.songId?.let { Listened(it, player.currentPosition, player.duration) }
            ?.let { listened ->
                musicDatabaseRepositoryImpl2.insertListened(listened)
            }

        mediaSession?.release()
        mediaSessionConnector?.setPlayer(null)
        playerNotificationManager?.setPlayer(null)
        playerNotificationManager = null
        player.release()
        disposable?.dispose()
        super.onDestroy()
    }
}