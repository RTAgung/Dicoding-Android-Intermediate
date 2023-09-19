package com.example.mymediaplayer

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.media.session.MediaButtonReceiver
import java.io.IOException


class MusicService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    companion object {
        const val EXTRA_MEDIA = "extra_media"
        val TAG = MusicService::class.simpleName
    }

    private val iBinder: IBinder = LocalBinder()
    private var isForegroundServiceRunning = false
    private var mediaFile: Int? = null

    private var notificationManager: NotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var notification: Notification? = null

    var mediaPlayer: MediaPlayer? = null
    val isReady: MutableLiveData<Boolean> = MutableLiveData()
    val isPlaying: MutableLiveData<Boolean> = MutableLiveData()

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    internal inner class LocalBinder : Binder() {
        val getService: MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
            Log.d(TAG, "onStartCommand: $intent")
            if (intent.action == null) {
                if (mediaFile == null) {
                    mediaFile = intent.extras?.getInt(EXTRA_MEDIA)
                }
                initMediaPlayer()
                initMediaSession()
                Log.d(TAG, "onStartCommand: ")
            }
        } catch (e: Exception) {
            stopSelf()
        }
        return START_STICKY
    }

    private fun initMediaSession() {
        Log.d(TAG, "initMediaSession: ")
        val mediaButtonReceiver = ComponentName(
            applicationContext,
            MediaButtonReceiver::class.java
        )

        mediaSession = MediaSessionCompat(this, "SOME_TAG", mediaButtonReceiver, null).also {
            it.setMetadata(getMetadata())
            it.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    Log.d(TAG, "onPlay: initMediaSession()")
                    playOrPauseMedia()
                }

                override fun onPause() {
                    Log.d(TAG, "onPause: initMediaSession()")
                    playOrPauseMedia()
                }
            })
            it.isActive = false
        }

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession!!.setMediaButtonReceiver(pendingIntent)
    }

    private fun showNotification() {
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description

        val builder = NotificationCompat.Builder(this, "1")
        builder
            .setContentTitle(description?.title)
            .setContentText(description?.subtitle)
            .setSubText(description?.description)
            .setContentIntent(controller?.sessionActivity)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSound(null)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)

        if (isAndroidOOrHigher()) {
            createNotificationChannel()
            builder.setChannelId("1")
        }

        if (mediaPlayer?.isPlaying == true) {
            builder.addAction(
                NotificationCompat.Action(
                    R.drawable.ic_media_pause,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )
        } else {
            builder.addAction(
                NotificationCompat.Action(
                    R.drawable.ic_media_play,
                    "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )
        }

        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                .setMediaSession(mediaSession?.sessionToken)
        )
        builder.setSmallIcon(R.drawable.ic_media_play)
        notification = builder.build()
        notification?.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        notificationManager?.notify(1, notification)
    }

    private fun isAndroidOOrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun createNotificationChannel() {
        val mChannel = NotificationChannel(
            "1",
            "channel_1",
            NotificationManager.IMPORTANCE_LOW
        )
        mChannel.description = "channel_1_desc"
        notificationManager?.createNotificationChannel(mChannel)
    }

    private fun initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer: ")
        isReady.postValue(false)

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        mediaPlayer?.reset()

        val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        mediaPlayer?.setAudioAttributes(attribute)

        if (mediaFile != null) {
            val afd = applicationContext.resources.openRawResourceFd(mediaFile!!)
            try {
                mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            } catch (e: IOException) {
                e.printStackTrace()
                stopSelf()
            }
        }

        mediaPlayer?.prepareAsync()
    }

    fun getMetadata(): MediaMetadataCompat {
        val builder = MediaMetadataCompat.Builder()
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist")
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "title")
        builder.putLong(
            MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer?.duration!!.toLong()
        )
        return builder.build()
    }

    private fun setMediaPlaybackState(state: Int) {
        val playbackstateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playbackstateBuilder.setState(
            state,
            (mediaPlayer?.currentPosition ?: 0).toLong(),
            1.0f,
            SystemClock.elapsedRealtime()
        )
        mediaSession?.setPlaybackState(playbackstateBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer?.release()
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        Log.d(TAG, "onPrepared: ")
        isReady.postValue(true)
        isPlaying.postValue(false)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        Log.d(TAG, "onCompletion: ")
        restartMedia()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Log.d(TAG, "onError: ")
        return false
    }

    fun playOrPauseMedia() {
        Log.d(TAG, "playOrPauseMedia: ")
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            showNotification()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            isPlaying.postValue(false)
        } else {
            mediaPlayer?.start()
            showNotification()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            if (!isForegroundServiceRunning) {
                startForeground(1, notification)
                isForegroundServiceRunning = true
            }
            isPlaying.postValue(true)
        }
        mediaSession?.isActive = true
    }

    fun stopMedia() {
        Log.d(TAG, "stopMedia: ")
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            restartMedia()
        }
    }

    fun restartMedia() {
        Log.d(TAG, "restartMedia: ")
        mediaPlayer?.reset()
        isPlaying.postValue(false)
        notificationManager?.cancelAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        isForegroundServiceRunning = false
        initMediaPlayer()
    }

    fun seekMedia(position: Int) {
        Log.d(TAG, "seekMedia: ")
        mediaPlayer?.seekTo(position)
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
    }
}