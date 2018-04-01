package com.gnest.remember.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.gnest.remember.App;
import com.gnest.remember.R;
import com.gnest.remember.view.activity.MainActivity;

public class AlarmService extends IntentService {

    public static final String NOTIFICATION_TEXT = "NotificationText";
    public static final String NOTIFICATION_MEMO_ID = "NotificationMemoPosition";
    public static final String NOTIFICATION_CHANNEL_ID = "com.gnest.remember.NOTIFICATION";
    public static final String IS_ON_MAIN_SCREEN = "isOnMainScreen";
    public static final long[] VIBRATE_PATTERN = {300, 300, 300, 300};

    public static Intent getServiceIntent(Context context, String notificationText, int savedId, boolean isOnMainScreen) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(NOTIFICATION_TEXT, notificationText);
        intent.putExtra(NOTIFICATION_MEMO_ID, savedId);
        intent.putExtra(IS_ON_MAIN_SCREEN, isOnMainScreen);
        return intent;
    }

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_note)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(intent.getStringExtra(NOTIFICATION_TEXT))
                    .setVibrate(VIBRATE_PATTERN)
                    .setSound(App.NOTIFICATION_SOUND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager);
                builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            }

            Intent resultIntent = new Intent(this, MainActivity.class);
            int memoId = intent.getIntExtra(NOTIFICATION_MEMO_ID, -1);
            boolean isOnMainScreen = intent.getBooleanExtra(IS_ON_MAIN_SCREEN, true);
            resultIntent.putExtra(NOTIFICATION_MEMO_ID, memoId);
            resultIntent.putExtra(IS_ON_MAIN_SCREEN, isOnMainScreen);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(memoId, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            if (notificationManager != null) {
                notificationManager.notify(memoId, builder.build());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(@Nullable NotificationManager notificationManager) {
        if (notificationManager != null) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(VIBRATE_PATTERN);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
