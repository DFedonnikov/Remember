package com.gnest.remember;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.preference.PreferenceManager;

import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.model.db.migration.RealmMigration;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    public static final String FONT_PATH = "fonts/CaviarDreams.ttf";
    public static final String FONT_SIZE_DEFAULT = "16";
    public static final String FONT_SIZE_KEY = "font_size";
    public static final String NOTIFICATION_SOUND_KEY = "notification_sound";
    public static Typeface FONT = Typeface.DEFAULT;
    public static int FONT_SIZE = 16;
    public static Uri NOTIFICATION_SOUND = Settings.System.DEFAULT_NOTIFICATION_URI;
    private static int[] NUM_OF_LINES;

    private static App sSelf;


    public static final Map<String, RealmConfiguration> REALM_CONFIG_MAP = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        Realm.init(this);
        configRealm();
        FONT = Typeface.createFromAsset(getAssets(), FONT_PATH);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        FONT_SIZE = Integer.parseInt(sharedPref.getString(FONT_SIZE_KEY, FONT_SIZE_DEFAULT));
        NOTIFICATION_SOUND = Uri.parse(sharedPref.getString(NOTIFICATION_SOUND_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()));
        NUM_OF_LINES = getResources().getIntArray(R.array.numOfLines);
    }

    private void configRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(MemoRealmFields.DEFAULT_CONFIG_NAME)
                .schemaVersion(2)
                .migration(new RealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);
        RealmConfiguration archive = new RealmConfiguration.Builder()
                .name(MemoRealmFields.ARCHIVE_CONFIG_NAME)
                .schemaVersion(2)
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

    public static int[] getNumOfLines() {
        return NUM_OF_LINES;
    }
}
