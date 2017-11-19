package com.gnest.remember;

import android.app.Application;
import android.content.Context;

import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.model.db.migration.RealmMigration;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static App sSelf;

    public static Context self() {
        return sSelf;
    }

    public static final Map<String, RealmConfiguration> REALM_CONFIG_MAP = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        Realm.init(this);
        configRealm();
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
}
