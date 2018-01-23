package com.gnest.remember.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.gnest.remember.R;
import com.gnest.remember.view.activity.MainActivity;

public class AlarmService extends IntentService {

    public static final String NOTIFICATION_TEXT = "NotificationText";
    public static final String NOTIFICATION_MEMO_ID = "NotificationMemoPosition";

    public static Intent getServiceIntent(Context context, String notificationText, long savedId) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(NOTIFICATION_TEXT, notificationText);
        intent.putExtra(NOTIFICATION_MEMO_ID, savedId);
        return intent;
    }

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_note)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(intent.getStringExtra(NOTIFICATION_TEXT));

            Intent resultIntent = new Intent(this, MainActivity.class);
            long memoId = intent.getLongExtra(NOTIFICATION_MEMO_ID, -1);
            resultIntent.putExtra(NOTIFICATION_MEMO_ID, memoId);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent((int) memoId, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = (int) memoId;
            notificationManager.notify(notificationId, builder.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
