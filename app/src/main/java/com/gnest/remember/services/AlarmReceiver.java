package com.gnest.remember.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import io.realm.Realm;
import io.realm.RealmResults;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String IS_SET = "isSet";
    private static final String IS_ON_MAIN_SCREEN = "isOnMainScreen";
    public static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    public static Intent getReceiverIntent(Context context, int id, String notificationText, long date, boolean isAlarmSet, boolean isOnMainScreen) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ID, id);
        intent.putExtra(TEXT, notificationText);
        intent.putExtra(DATE, date);
        intent.putExtra(IS_SET, isAlarmSet);
        intent.putExtra(IS_ON_MAIN_SCREEN, isOnMainScreen);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOnMainScreen = intent.getBooleanExtra(IS_ON_MAIN_SCREEN, true);
        if (BOOT_COMPLETED_ACTION.equals(intent.getAction())) {
            resetAlarm(context, isOnMainScreen);
        } else {
            int id = intent.getIntExtra(ID, -1);
            String text = intent.getStringExtra(TEXT);
            long date = intent.getLongExtra(DATE, 0);
            boolean isSet = intent.getBooleanExtra(IS_SET, false);
            setAlarm(context, id, text, date, isSet, isOnMainScreen);
        }
    }


    private void setAlarm(Context context, int id, String text, long date, boolean isSet, boolean isOnMainScreen) {
        Intent alarmServiceIntent = AlarmService.getServiceIntent(context, text, id, isOnMainScreen);
        PendingIntent pendingIntent = PendingIntent.getService(context, id, alarmServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            if (isSet) {
                if (Build.VERSION.SDK_INT >= 23) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    manager.setExact(AlarmManager.RTC_WAKEUP, date, pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
                }
            } else {
                manager.cancel(pendingIntent);
            }
        }
    }

    private void resetAlarm(Context context, boolean isOnMainScreen) {
        Realm realm = null;
        try {
            if (isOnMainScreen) {
                realm = Realm.getDefaultInstance();
            } else {
                realm = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME));
            }
            realm.executeTransactionAsync(realm1 -> {
                RealmResults<Memo> results = realm1.where(Memo.class)
                        .findAllSorted(MemoRealmFields.ID);
                for (Memo memo : results) {
                    if (memo.isAlarmSet()) {
                        setAlarm(context, memo.getId(), memo.getMemoText(), memo.getAlarmDate(), memo.isAlarmSet(), isOnMainScreen);
                    }
                }
            });

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
