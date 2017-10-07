package com.gnest.remember.model.db.migration;

import android.support.annotation.NonNull;

import com.gnest.remember.model.db.data.MemoRealmFields;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create(MemoRealmFields.SCHEMA_NAME)
                    .addField(MemoRealmFields.ID, int.class, FieldAttribute.PRIMARY_KEY)
                    .addField(MemoRealmFields.MEMO_TEXT, String.class)
                    .addField(MemoRealmFields.POSITION, int.class)
                    .addField(MemoRealmFields.COLOR, String.class)
                    .addField(MemoRealmFields.IS_ALARM_SET, boolean.class)
                    .addField(MemoRealmFields.IS_SELECTED, boolean.class)
                    .addField(MemoRealmFields.IS_EXPANDED, boolean.class);

            oldVersion++;
        }
        if (oldVersion == 1) {
            schema.get(MemoRealmFields.SCHEMA_NAME)
                    .addField(MemoRealmFields.ALARM_DATE, long.class);

            oldVersion++;
        }
    }
}
