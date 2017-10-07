package com.gnest.remember;

import android.app.Application;
import android.content.Context;

import com.gnest.remember.model.db.migration.RealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static App sSelf;

    public static Context self() {
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("memo.realm")
                .schemaVersion(2)
                .migration(new RealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
