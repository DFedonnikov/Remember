package com.gnest.remember.model;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import io.realm.Realm;
import rx.Observable;

public class ArchiveMemoModelImpl extends ListFragmentModelImpl {

    @Override
    public void openDB() {
        primaryRealm = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME));
        secondaryRealm = Realm.getDefaultInstance();
    }

    @Override
    public Observable<Memo> deleteMemo(int memoId) {
        Memo toRemove = primaryRealm.where(Memo.class)
                .equalTo(MemoRealmFields.ID, memoId)
                .findFirst();
        if (toRemove == null) {
            throw new IllegalStateException("Cannot find memo with id " + memoId);
        }
        // Creating new Memo object because after deletion toRemove will become invalid to operate on.
        Memo toReturn = new Memo(toRemove.getId(), toRemove.getMemoText(), toRemove.getPosition(), toRemove.getColor(), toRemove.getAlarmDate(), toRemove.isAlarmSet());

        removeFromRealm(primaryRealm, toRemove);

        return Observable.just(toReturn);
    }

    @Override
    public void revertDeleteMemo(Memo toRevert) {
        insertToRealm(primaryRealm, toRevert);
    }

    @Override
    public void setMemoAlarmFalse(int id) {
        //Alarm already set off when moving memo from main list to archived
        throw new UnsupportedOperationException();
    }
}
