package com.gnest.remember.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.gnest.remember.App;
import com.gnest.remember.R;
import com.gnest.remember.view.activity.MainActivity;

public class AlarmService extends IntentService {

    public static final String NOTIFICATION_TEXT = "NotificationText";
    public static final String NOTIFICATION_MEMO_ID = "NotificationMemoPosition";
    public static final String IS_ON_MAIN_SCREEN = "isOnMainScreen";

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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_note)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(intent.getStringExtra(NOTIFICATION_TEXT));
            /*If user uses Android Oreo or higher,
            notification channel will take care of sound and vibration.
            Otherwise, configure them in builder.
             */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(App.VIBRATE_PATTERN)
                        .setSound(App.NOTIFICATION_SOUND);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
