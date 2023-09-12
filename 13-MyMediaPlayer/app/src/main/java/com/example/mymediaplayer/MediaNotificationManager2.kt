package com.example.mymediaplayer
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Intent
//import android.os.Build
//import android.support.v4.media.MediaDescriptionCompat
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.core.content.ContextCompat
//import androidx.media.session.MediaButtonReceiver
//
//
//class MediaNotificationManager2(private val mService: MediaPlayerService) {
//    companion object {
//        const val NOTIFICATION_ID = 1
//
//        val TAG = MediaNotificationManager2::class.simpleName
//        const val CHANNEL_ID = "channel_01"
//        const val CHANNEL_NAME = "channel_01"
//        const val REQUEST_CODE = 1
//    }
//
//    private var mNotificationManager: NotificationManager =
//        mService.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
//
//    private var mPlayAction: NotificationCompat.Action = NotificationCompat.Action(
//        R.drawable.baseline_play_arrow_24,
//        "play",
//        MediaButtonReceiver.buildMediaButtonPendingIntent(
//            mService, PlaybackStateCompat.ACTION_PLAY
//        )
//    )
//
//    private var mPauseAction: NotificationCompat.Action = NotificationCompat.Action(
//        R.drawable.baseline_pause_24,
//        "pause",
//        MediaButtonReceiver.buildMediaButtonPendingIntent(
//            mService, PlaybackStateCompat.ACTION_PAUSE
//        )
//    )
//
//    init {
//        mNotificationManager.cancelAll()
//    }
//
//    fun onDestroy() {
//        Log.d(TAG, "onDestroy: ")
//    }
//
//    fun getNotification(
//        metadata: MediaMetadataCompat, state: PlaybackStateCompat, token: MediaSessionCompat.Token
//    ): Notification {
//        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
//        val description = metadata.description
//        val builder: NotificationCompat.Builder =
//            buildNotification(state, token, isPlaying, description)
//        return builder.build()
//    }
//
//    private fun buildNotification(
//        state: PlaybackStateCompat,
//        token: MediaSessionCompat.Token,
//        isPlaying: Boolean,
//        description: MediaDescriptionCompat
//    ): NotificationCompat.Builder {
//        val builder = NotificationCompat.Builder(mService, CHANNEL_ID)
//        builder
//            .setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(token)
//                    .setShowActionsInCompactView(0)
////                    .setShowCancelButton(true)
////                    .setCancelButtonIntent(
////                        MediaButtonReceiver.buildMediaButtonPendingIntent(
////                            mService, PlaybackStateCompat.ACTION_STOP
////                        )
////                    )
//            )
//            .setColor(ContextCompat.getColor(mService, android.R.color.holo_blue_dark))
//            .setSmallIcon(R.drawable.baseline_music_note_24)
//            .setContentIntent(createContentIntent())
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setContentTitle("Track 1")
//            .setContentText("Song content...")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setSound(null)
//            .setOngoing(true)
//            .setAutoCancel(false)
////            .setDeleteIntent(
////                MediaButtonReceiver.buildMediaButtonPendingIntent(
////                    mService, PlaybackStateCompat.ACTION_PAUSE
////                )
////            )
//        if (isAndroidOOrHigher()) {
//            createChannel()
//            builder.setChannelId(CHANNEL_ID)
//        }
//        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)
//        return builder
//    }
//
//    private fun createChannel() {
//        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
//            val name = "media player channel"
//            val description = "MediaSession and MediaPlayer"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
//            mChannel.description = description
//            mNotificationManager.createNotificationChannel(mChannel)
//            Log.d(TAG, "createChannel: New channel created")
//        } else {
//            Log.d(TAG, "createChannel: Existing channel reused")
//        }
//    }
//
//    private fun isAndroidOOrHigher(): Boolean {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//    }
//
//    private fun createContentIntent(): PendingIntent? {
//        val openUI = Intent(mService, MainActivity::class.java)
//        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
//        return PendingIntent.getActivity(
//            mService,
//            REQUEST_CODE,
//            openUI,
//            pendingFlags
//        )
//    }
//}