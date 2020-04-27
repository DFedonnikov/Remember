package com.gnest.remember.data.datasources;

import com.gnest.remember.model.db.data.Memo;

import io.realm.Realm;

public class ArchiveLocalDataSource extends MainLocalDataSource {

    public ArchiveLocalDataSource(Realm primaryRealm, Realm secondaryRealm) {
        super(primaryRealm, secondaryRealm);
    }

    @Override
    public void openDB() {
    }

    @Override
    public void revertDeleteMemo(Memo toRevert) {
        insertToRealm(primaryRealm, toRevert);
    }

}
