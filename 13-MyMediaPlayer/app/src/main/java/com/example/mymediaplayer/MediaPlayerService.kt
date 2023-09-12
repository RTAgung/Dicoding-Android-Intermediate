package com.example.mymediaplayer
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Intent
//import android.media.AudioAttributes
//import android.media.MediaPlayer
//import android.os.Binder
//import android.os.Build
//import android.os.IBinder
//import android.os.SystemClock
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import java.io.IOException
//
//
//class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener,
//    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
//
//    companion object {
//        const val EXTRA_MEDIA = "extra_media"
//        val TAG = MediaPlayerService::class.simpleName
//    }
//
//    private val iBinder: IBinder = LocalBinder()
//    private var isForegroundServiceRunning = false
//    private var mediaFile: Int? = null
//
//    private var mMediaNotificationManager2: MediaNotificationManager2? = null
//    private var notificationManager: NotificationManager? = null
//    private var mediaSession: MediaSessionCompat? = null
//    private var stateBuilder: PlaybackStateCompat.Builder? = null
//
//    var mediaPlayer: MediaPlayer? = null
//    val isReady: MutableLiveData<Boolean> = MutableLiveData()
//    val isPlaying: MutableLiveData<Boolean> = MutableLiveData()
//
//    override fun onBind(intent: Intent): IBinder {
//        return iBinder
//    }
//
//    internal inner class LocalBinder : Binder() {
//        val getService: MediaPlayerService = this@MediaPlayerService
//    }
//
//    override fun onCreate() {
//        super.onCreate()
////        notificationManager =
////            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        createMediaSession()
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//
////        mPlayAction = NotificationCompat.Action(
////            R.drawable.baseline_play_arrow_24,
////            "play",
////            MediaButtonReceiver.buildMediaButtonPendingIntent(
////                this, PlaybackStateCompat.ACTION_PLAY
////            )
////        )
////        mPauseAction = NotificationCompat.Action(
////            R.drawable.baseline_pause_24,
////            "pause",
////            MediaButtonReceiver.buildMediaButtonPendingIntent(
////                this, PlaybackStateCompat.ACTION_PAUSE
////            )
////        )
//
//        try {
//            mediaFile = intent.extras?.getInt(EXTRA_MEDIA)
//        } catch (e: Exception) {
//            stopSelf()
//        }
//
//        if (mediaFile != null) {
//            initMediaPlayer()
//            mMediaNotificationManager2 = MediaNotificationManager2(this)
//            mediaSession = MediaSessionCompat(this, "SOME_TAG").also {
//                it.setMetadata(getMetadata())
//                it.setCallback(object : MediaSessionCompat.Callback() {
//                    override fun onPlay() {
//                        playOrPauseMedia()
//                    }
//
//                    override fun onPause() {
//                        playOrPauseMedia()
//                    }
//                })
//            }
//
//        } else stopSelf()
//
//        return START_STICKY
//    }
//
////    private fun buildNotification2(): NotificationCompat.Builder {
////        var icon: Int?
////        var playOrPause: String?
////        if (mediaPlayer?.isPlaying == true) {
////            icon = R.drawable.baseline_pause_24
////            playOrPause = "pause"
////        } else {
////            icon = R.drawable.baseline_pause_24
////            playOrPause = "play"
////        }
////
////        val playOrPauseAction: NotificationCompat.Action = NotificationCompat.Action(
////            icon, playOrPause,
////            MediaButtonReceiver.buildMediaButtonPendingIntent(
////                this,
////                PlaybackStateCompat.ACTION_PLAY_PAUSE
////            )
////        )
////
////        val notificationBuilder =
////            NotificationCompat.Builder(this, MediaNotificationManager.CHANNEL_ID)
////                .setContentTitle("Track title")
////                .setContentText("Artist - Album")
////                .setSmallIcon(R.drawable.baseline_music_note_24)
////                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////                .setOngoing(true)
////                .setSound(null)
////                .setPriority(NotificationCompat.PRIORITY_HIGH)
////                .setContentIntent(createContentIntent())
////                .setStyle(
////                    MediaStyle()
////                        .setMediaSession(mediaSession?.sessionToken)
////                        .setShowActionsInCompactView(0)
////                )
//////                .addAction(playOrPauseAction)
////
////        if (isAndroidOOrHigher()) {
////            createNotificationChannel()
////            notificationBuilder.setChannelId(MediaNotificationManager.CHANNEL_ID)
////        }
////
////        return notificationBuilder
////    }
////
////    private fun getNotification2(): Notification {
////        return buildNotification2().build()
////    }
//
////    private var mPlayAction: NotificationCompat.Action? = null
////
////    private var mPauseAction: NotificationCompat.Action? = null
////
//
//    private fun isAndroidOOrHigher(): Boolean {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//    }
//
//    private fun createNotificationChannel() {
//        val mChannel = NotificationChannel(
//            MediaNotificationManager2.CHANNEL_ID,
//            MediaNotificationManager2.CHANNEL_NAME,
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        mChannel.description = MediaNotificationManager2.CHANNEL_NAME
//        notificationManager?.createNotificationChannel(mChannel)
//    }
//
//    private fun createContentIntent(): PendingIntent {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
//        return PendingIntent.getActivity(
//            this,
//            MediaNotificationManager2.REQUEST_CODE,
//            notificationIntent,
//            pendingFlags
//        )
//    }
//
//    private fun createMediaSession() {
//        stateBuilder = PlaybackStateCompat.Builder()
//            .setActions(
//                PlaybackStateCompat.ACTION_PLAY or
//                        PlaybackStateCompat.ACTION_PAUSE
//            )
//
//        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
//        val pendingIntent = PendingIntent.getBroadcast(
//            baseContext,
//            0, mediaButtonIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        mediaSession = MediaSessionCompat(this, "SOME_TAG", null, pendingIntent).also {
//            it.isActive = true
//            it.setMediaButtonReceiver(null)
//            it.setPlaybackState(stateBuilder?.build())
//            it.setCallback(object : MediaSessionCompat.Callback() {
//                override fun onPlay() {
//                    playOrPauseMedia()
//                }
//
//                override fun onPause() {
//                    playOrPauseMedia()
//                }
//            })
//        }
//    }
////
////    private fun buildNotification(
////        state: PlaybackStateCompat,
////        token: MediaSessionCompat.Token,
////        isPlaying: Boolean,
////        description: MediaDescriptionCompat
////    ): NotificationCompat.Builder {
////        val builder = NotificationCompat.Builder(this, MediaNotificationManager.CHANNEL_ID)
////        builder
////            .setStyle(
////                androidx.media.app.NotificationCompat.MediaStyle()
////                    .setMediaSession(token)
////                    .setShowActionsInCompactView(0)
////                    .setShowCancelButton(true)
////                    .setCancelButtonIntent(
////                        MediaButtonReceiver.buildMediaButtonPendingIntent(
////                            this, PlaybackStateCompat.ACTION_STOP
////                        )
////                    )
////            )
////            .setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
////            .setSmallIcon(R.drawable.baseline_music_note_24)
////            .setContentTitle(description.title)
////            .setDeleteIntent(
////                MediaButtonReceiver.buildMediaButtonPendingIntent(
////                    this, PlaybackStateCompat.ACTION_PAUSE
////                )
////            )
////        if (isAndroidOOrHigher()) {
////            createChannel()
////            builder.setChannelId(MediaNotificationManager.CHANNEL_ID)
////        }
////        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)
////        return builder
////    }
////
////    fun getNotification(
////        metadata: MediaMetadataCompat, state: PlaybackStateCompat, token: MediaSessionCompat.Token
////    ): Notification {
////        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
////        val description = metadata.description
////        val builder: NotificationCompat.Builder =
////            buildNotification(state, token, isPlaying, description)
////        return builder.build()
////    }
//
//    private fun initMediaPlayer() {
//        Log.d(TAG, "initMediaPlayer: ")
//        isReady.postValue(false)
//
//        mediaPlayer = MediaPlayer()
//        mediaPlayer?.setOnPreparedListener(this)
//        mediaPlayer?.setOnCompletionListener(this)
//        mediaPlayer?.setOnErrorListener(this)
//        mediaPlayer?.reset()
//
//        val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
//        mediaPlayer?.setAudioAttributes(attribute)
//
//        if (mediaFile != null) {
//            val afd = applicationContext.resources.openRawResourceFd(mediaFile!!)
//            try {
//                mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//            } catch (e: IOException) {
//                e.printStackTrace()
//                stopSelf()
//            }
//        }
//
//        mediaPlayer?.prepareAsync()
//
////        mMediaNotificationManager = MediaNotificationManager(MediaPlayerService())
////        mNotificationManager?.cancelAll()
////
////        mediaSession = MediaSessionCompat(this, "SOME_TAG")
////        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
////            override fun onPlay() {
////                playMedia()
////            }
////
////            override fun onPause() {
////                pauseMedia()
////            }
////        })
//    }
//
//    fun getMetadata(): MediaMetadataCompat {
//        val builder = MediaMetadataCompat.Builder()
//        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist")
//        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "title")
//        builder.putLong(
//            MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer?.duration!!.toLong()
//        )
//        return builder.build()
//    }
//
//    private fun getState(): PlaybackStateCompat {
//        val actions =
//            if (mediaPlayer?.isPlaying == true) PlaybackStateCompat.ACTION_PAUSE
//            else PlaybackStateCompat.ACTION_PLAY
//        val state =
//            if (mediaPlayer?.isPlaying == true) PlaybackStateCompat.STATE_PLAYING
//            else PlaybackStateCompat.STATE_PAUSED
//        val stateBuilder = PlaybackStateCompat.Builder()
//        stateBuilder.setActions(actions)
//        stateBuilder.setState(
//            state,
//            mediaPlayer?.currentPosition!!.toLong(),
//            1.0f,
//            SystemClock.elapsedRealtime()
//        )
//        return stateBuilder.build()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "onDestroy: ")
//        if (mediaPlayer != null) {
//            stopMedia()
//            mediaPlayer?.release()
//        }
//    }
//
//    override fun onPrepared(mediaPlayer: MediaPlayer?) {
//        Log.d(TAG, "onPrepared: ")
//        isReady.postValue(true)
//        isPlaying.postValue(false)
//    }
//
//    override fun onCompletion(p0: MediaPlayer?) {
//        Log.d(TAG, "onCompletion: ")
//        restartMedia()
//    }
//
//    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
//        Log.d(TAG, "onError: ")
//        return false
//    }
//
//    fun playOrPauseMedia() {
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.pause()
//            isPlaying.postValue(false)
//        } else {
//            if (!isForegroundServiceRunning) {
////                val notification = getNotification(
////                    getMetadata(),
////                    getState(),
////                    mediaSession?.sessionToken ?: MediaSessionCompat(this, "SOME_TAG").sessionToken
////                )
////                val notification = buildNotification2()
//                val notification = mediaSession?.sessionToken?.let {
//                    mMediaNotificationManager2?.getNotification(getMetadata(), getState(),
//                        it
//                    )
//                }
//                startForeground(MediaNotificationManager2.NOTIFICATION_ID, notification)
//            }
//            mediaPlayer?.start()
//            isPlaying.postValue(true)
//            isForegroundServiceRunning = true
//        }
//    }
//
//    fun playMedia() {
//        Log.d(TAG, "playMedia: ")
//        if (mediaPlayer?.isPlaying == false) {
//            if (!isForegroundServiceRunning) {
////                val notification = getNotification(
////                    getMetadata(),
////                    getState(),
////                    mediaSession?.sessionToken ?: MediaSessionCompat(this, "SOME_TAG").sessionToken
////                )
////                val notification = buildNotification2()
////                val notification = getNotification2()
////                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
//            }
//            mediaPlayer?.start()
//            isPlaying.postValue(true)
//            isForegroundServiceRunning = true
//        }
//    }
//
//    fun stopMedia() {
//        Log.d(TAG, "stopMedia: ")
//        if (mediaPlayer == null) return
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.stop()
//            restartMedia()
//        }
//    }
//
//    fun restartMedia() {
//        Log.d(TAG, "restartMedia: ")
//        mediaPlayer?.reset()
//        isPlaying.postValue(false)
//        stopForeground(STOP_FOREGROUND_REMOVE)
//        isForegroundServiceRunning = false
//        initMediaPlayer()
//    }
//
//    fun pauseMedia() {
//        Log.d(TAG, "pauseMedia: ")
//        if (mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.pause()
//            isPlaying.postValue(false)
//        }
//    }
//
//    fun resumeMedia() {
//        Log.d(TAG, "resumeMedia: ")
//        if (mediaPlayer?.isPlaying == false) {
//            mediaPlayer?.start()
//            isPlaying.postValue(true)
//        }
//    }
//
//    fun seekMedia(position: Int) {
//        Log.d(TAG, "seekMedia: ")
//        mediaPlayer?.seekTo(position)
//    }
//}