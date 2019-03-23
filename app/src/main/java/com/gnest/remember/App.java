package com.gnest.remember;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.model.db.migration.RealmMigration;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    public static final String FONT_PATH = "fonts/CaviarDreams.ttf";
    public static final String FONT_SIZE_DEFAULT = "16";
    public static final String FONT_SIZE_KEY = "font_size";
    public static final String NOTIFICATION_SOUND_KEY = "notification_sound";
    public static final String NOTIFICATION_CHANNEL_ID = "com.gnest.remember.NOTIFICATION";
    public static final long[] VIBRATE_PATTERN = {300, 300, 300, 300};
    public static Typeface FONT = Typeface.DEFAULT;
    public static int FONT_SIZE = 16;
    public static Uri NOTIFICATION_SOUND = Settings.System.DEFAULT_NOTIFICATION_URI;
    private static int[] NUM_OF_LINES;
    public static final Map<String, RealmConfiguration> REALM_CONFIG_MAP = new HashMap<>();

    private static App sSelf;

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        sSelf = this;
        Realm.init(this);
        configRealm();
        FONT = Typeface.createFromAsset(getAssets(), FONT_PATH);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        FONT_SIZE = Integer.parseInt(sharedPref.getString(FONT_SIZE_KEY, FONT_SIZE_DEFAULT));
        NOTIFICATION_SOUND = Uri.parse(sharedPref.getString(NOTIFICATION_SOUND_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()));
        NUM_OF_LINES = getResources().getIntArray(R.array.numOfLines);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        Fabric.with(this, new Crashlytics());
    }

    private void configRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(MemoRealmFields.DEFAULT_CONFIG_NAME)
                .schemaVersion(1)
                .migration(new RealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);
        RealmConfiguration archive = new RealmConfiguration.Builder()
                .name(MemoRealmFields.ARCHIVE_CONFIG_NAME)
                .schemaVersion(1)
                .migration(new RealmMigration())
                .build();
        REALM_CONFIG_MAP.put(MemoRealmFields.DEFAULT_CONFIG_NAME, config);
        REALM_CONFIG_MAP.put(MemoRealmFields.ARCHIVE_CONFIG_NAME, archive);
    }

    public static RealmConfiguration getConfigurationByName(String name) {
        return REALM_CONFIG_MAP.get(name);
    }

    public static Context self() {
        return sSelf;
    }

    public static void setFontSize(int fontSize) {
        FONT_SIZE = fontSize;
    }

    public static void setNotificationSound(Uri notificationSoundPath) {
        NOTIFICATION_SOUND = notificationSoundPath;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            if (channel == null) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                        .build();
                channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
                channel.enableVibration(true);
                channel.setVibrationPattern(VIBRATE_PATTERN);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static int[] getNumOfLines() {
        return NUM_OF_LINES;
    }


    public static RefWatcher getRefWatcher() {
        return sSelf.refWatcher;
    }
}