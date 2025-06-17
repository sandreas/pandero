package com.pilabor.pandero.utils


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pilabor.pandero.MainActivity
import com.pilabor.pandero.data.model.Song
import com.pilabor.pandero.service.MediaPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.net.URL

@Single
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "musify_notification_channel"
        const val CHANNEL_NAME = "Musify Notification Channel"
        const val CHANNEL_DESCRIPTION = "Channel for Musify playback notifications"
        const val NOTIFICATION_ID = 1

        @SuppressLint("ObsoleteSdkInt")
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    setSound(null, null)
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun createPlayerNotification(
        isPlaying: Boolean,
        song: Song, mediaSession: MediaSessionCompat, callBack: (Notification) -> Unit
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist.name)
            // .setSmallIcon(com.codewithfk.musify_android.R.drawable.ic_profile)
            .setContentIntent(pendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        val prevIntent = Intent(context, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_PREVIOUS
        }
        val nextIntent = Intent(context, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_NEXT
        }
        val playIntent = Intent(context, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_PLAY
        }
        val pauseIntent = Intent(context, MediaPlayerService::class.java).apply {
            action = MediaPlayerService.ACTION_PAUSE
        }

        val prevPendingIntent = PendingIntent.getService(
            context,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextPendingIntent = PendingIntent.getService(
            context,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPendingIntent = PendingIntent.getService(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getService(
            context,
            0,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "Previous",
                prevPendingIntent
            )
        )
        notificationBuilder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
        )

        if (isPlaying) {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    pausePendingIntent
                )
            )
        } else {
            notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    playPendingIntent
                )
            )
        }

        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        callBack(notification)
        loadAlbumIcon(notificationBuilder, song.coverImage, callBack)

    }

    fun loadAlbumIcon(
        builder: NotificationCompat.Builder, url: String, callBack: (Notification) -> Unit
    ) {
        try {
            scope.launch {

                val bitmap = withContext(Dispatchers.IO) {
                    try {
                        val imageUrl = URL(url)
                        val connection = imageUrl.openConnection()
                        connection.connect()
                        val input = connection.getInputStream()
                        val bitmap = BitmapFactory.decodeStream(input)

                        bitmap
                    } catch (_: Exception) {
                        null
                    }
                }
                bitmap?.let {
                    builder.setLargeIcon(it)
                    callBack(builder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateNotification(notification: Notification) {
        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}