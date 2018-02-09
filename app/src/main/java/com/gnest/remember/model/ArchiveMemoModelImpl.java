package com.gnest.remember.model;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import io.realm.Realm;

public class ArchiveMemoModelImpl extends ListFragmentModelImpl {

    @Override
    public void openDB() {
        primaryRealm = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME));
        secondaryRealm = Realm.getDefaultInstance();
    }

    @Override
    public void revertDeleteMemo(Memo toRevert) {
        insertToRealm(primaryRealm, toRevert);
    }

}
