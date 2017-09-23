package com.gnest.remember;

import android.app.Application;
import android.content.Context;

import com.gnest.remember.model.db.migration.RealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by DFedonnikov on 11.08.2017.
 */

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
                .schemaVersion(1)
                .migration(new RealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
