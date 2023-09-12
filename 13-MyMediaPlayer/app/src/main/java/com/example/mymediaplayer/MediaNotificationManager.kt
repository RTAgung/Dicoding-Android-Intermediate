package com.example.mymediaplayer
//
//import android.R
//import android.app.Notification
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import androidx.core.app.NotificationCompat
//
//
//class MediaNotificationManager(private val service: MediaPlayerService) : BroadcastReceiver() {
//    private val NOTIFICATION_ID = 412
//    private val REQUEST_CODE = 100
//
//    private val ACTION_PAUSE = "com.example.android.musicplayercodelab.pause"
//    private val ACTION_PLAY = "com.example.android.musicplayercodelab.play"
//    private val ACTION_NEXT = "com.example.android.musicplayercodelab.next"
//    private val ACTION_PREV = "com.example.android.musicplayercodelab.prev"
//
//    private var mNotificationManager: NotificationManager? = null
//
//    private var mPlayAction: NotificationCompat.Action? = null
//    private var mPauseAction: NotificationCompat.Action? = null
//    private var mNextAction: NotificationCompat.Action? = null
//    private var mPrevAction: NotificationCompat.Action? = null
//
//    private var mStarted = false
//
//    init {
//        val pkg: String = service.packageName
//        val playIntent = PendingIntent.getBroadcast(
//            service,
//            REQUEST_CODE,
//            Intent(ACTION_PLAY).setPackage(pkg),
//            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        val pauseIntent = PendingIntent.getBroadcast(
//            service,
//            REQUEST_CODE,
//            Intent(ACTION_PAUSE).setPackage(pkg),
//            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        mPlayAction = NotificationCompat.Action(
//            R.drawable.ic_media_play,
//            "play",
//            playIntent
//        )
//        mPauseAction = NotificationCompat.Action(
//            R.drawable.ic_media_pause,
//            "pause",
//            pauseIntent
//        )
//        val filter = IntentFilter()
//        filter.addAction(ACTION_NEXT)
//        filter.addAction(ACTION_PAUSE)
//        filter.addAction(ACTION_PLAY)
//        filter.addAction(ACTION_PREV)
//        service.registerReceiver(this, filter)
//        mNotificationManager =
//            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Cancel all notifications to handle the case where the Service was killed and
//        // restarted by the system.
//        mNotificationManager!!.cancelAll()
//    }
//
//    override fun onReceive(context: Context?, intent: Intent) {
//        when (intent.action) {
//            ACTION_PAUSE -> service.playOrPauseMedia()
//            ACTION_PLAY -> service.playOrPauseMedia()
//        }
//    }
//
//    fun update(
//        metadata: MediaMetadataCompat?,
//        state: PlaybackStateCompat?,
//        token: MediaSessionCompat.Token?
//    ) {
//        if (state == null || state.state == PlaybackStateCompat.STATE_STOPPED || state.state == PlaybackStateCompat.STATE_NONE) {
//            service.stopForeground(true)
//            try {
//                service.unregisterReceiver(this)
//            } catch (ex: IllegalArgumentException) {
//                // ignore receiver not registered
//            }
//            service.stopSelf()
//            return
//        }
//        if (metadata == null) {
//            return
//        }
//        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
//        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(service)
//        val description = metadata.description
//        notificationBuilder
//            .setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(token)
//                    .setShowActionsInCompactView(0, 1, 2)
//            )
//            .setColor(
//                service.application.resources.getColor(R.color.holo_blue_dark)
//            )
//            .setSmallIcon(R.drawable.ic_media_play)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setContentIntent(createContentIntent())
//            .setContentTitle(description.title)
//            .setContentText(description.subtitle)
//            .setOngoing(isPlaying)
//            .setWhen(if (isPlaying) System.currentTimeMillis() - state.position else 0)
//            .setShowWhen(isPlaying)
//            .setUsesChronometer(isPlaying)
//
//        // If skip to next action is enabled
//        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
//            notificationBuilder.addAction(mPrevAction)
//        }
//        notificationBuilder.addAction(if (isPlaying) mPauseAction else mPlayAction)
//
//        // If skip to prev action is enabled
//        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
//            notificationBuilder.addAction(mNextAction)
//        }
//        val notification: Notification = notificationBuilder.build()
//        if (isPlaying && !mStarted) {
//            service.startService(
//                Intent(
//                    service.applicationContext,
//                    MediaPlayerService::class.java
//                )
//            )
//            service.startForeground(NOTIFICATION_ID, notification)
//            mStarted = true
//        } else {
//            if (!isPlaying) {
//                service.stopForeground(false)
//                mStarted = false
//            }
//            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
//        }
//    }
//
//    private fun createContentIntent(): PendingIntent? {
//        val openUI = Intent(service, MainActivity::class.java)
//        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        return PendingIntent.getActivity(
//            service, REQUEST_CODE, openUI,
//            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//}