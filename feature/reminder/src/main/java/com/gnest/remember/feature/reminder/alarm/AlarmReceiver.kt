package com.gnest.remember.feature.reminder.alarm

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gnest.remember.core.common.extensions.getParcelable
import com.gnest.remember.core.designsystem.icon.RememberIcons
import com.gnest.remember.feature.reminder.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainActivityClass: Class<*>

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmInfo = intent?.getParcelable<AlarmInfo>(AlarmClockImpl.ALARM_INFO_KEY) ?: return
        val builder = NotificationCompat.Builder(this.context, "com.gnest.remember.NOTIFICATION")
            .setSmallIcon(RememberIcons.Idea)
            .setContentTitle(this.context.getString(R.string.reminder_title))
            .setContentText(alarmInfo.text?.takeIf { it.isNotEmpty() } ?: this.context.getString(R.string.default_reminder_text))
            .setPriority(alarmInfo.priority)
            .setAutoCancel(alarmInfo.autoCancel)
        if (alarmInfo.isFullscreen) {
            val fullscreenIntent = Intent(context, mainActivityClass)
            val fullscreenPendingIntent =
                PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE));
            builder.setFullScreenIntent(fullscreenPendingIntent, true)
        }
        NotificationManagerCompat.from(this.context).notify(alarmInfo.id.toInt(), builder.build())
    }
}