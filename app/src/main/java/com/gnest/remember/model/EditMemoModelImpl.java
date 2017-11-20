package com.gnest.remember.model;

import android.support.v4.util.Pair;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class EditMemoModelImpl implements IEditMemoModel {

    private static Calendar sSelectedDate = Calendar.getInstance();
    private final BehaviorSubject<Boolean> dataSavedSubject = BehaviorSubject.create();

    private Realm realm;
    private int mMemoId;
    private boolean isNew;
    private boolean wasAlarmSet;
    private boolean isAlarmSet;


    public EditMemoModelImpl(int memoId) {
        this.mMemoId = memoId;
        this.isAlarmSet = false;
        this.isNew = memoId == -1;
    }

    @Override
    public void openDB() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void closeDB() {
        realm.close();
    }

    @Override
    public Memo getData() {
        Memo memo = getEditedMemo();
        wasAlarmSet = memo.isAlarmSet();
        sSelectedDate.setTimeInMillis(memo.getAlarmDate());
        return memo;
    }

    @Override
    public Memo getEditedMemo() {
        return realm.where(Memo.class)
                .equalTo(MemoRealmFields.ID, mMemoId)
                .findFirst();
    }

    @Override
    public Observable<Pair<Integer, Integer>> saveMemoToDB(String memoText, String memoColor) {
        if (isNew) {
            if (memoText.isEmpty()) {
                return Observable.just(new Pair<>(-1, -1));
            }
            insertNewMemo(memoText, memoColor);
        } else {
            updateMemo(memoText, memoColor);
        }

        return dataSavedSubject
                .subscribeOn(Schedulers.computation())
                .distinctUntilChanged()
                .flatMap(dataSaved -> {
                    {
                        if (isNew) {
                            int id = -1;
                            Number idNumber = realm.where(Memo.class)
                                    .max(MemoRealmFields.ID);
                            if (idNumber != null) {
                                id = idNumber.intValue();
                            }
                            return Observable.just(new Pair<>(id, -1));
                        } else {
                            Memo memo = getEditedMemo();
                            return Observable.just(new Pair<>(memo.getId(), memo.getPosition()));
                        }
                    }
                });
    }

    private void insertNewMemo(String memoText, String memoColor) {
        realm.executeTransactionAsync(realm1 -> {
            int idMain = 0;
            int idArchived = 0;
            int position = 0;

            Number idNumberMain = realm1.where(Memo.class)
                    .max(MemoRealmFields.ID);
            if (idNumberMain != null) {
                idMain = idNumberMain.intValue() + 1;
            }
            Realm realmAchrived = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME));
            Number idNumberArchived = realmAchrived.where(Memo.class)
                    .max(MemoRealmFields.ID);
            realmAchrived.close();
            if (idNumberArchived != null) {
                idArchived = idNumberArchived.intValue() + 1;
            }
            int id = Math.max(idMain, idArchived);
            Number positionNumber = realm1.where(Memo.class)
                    .max(MemoRealmFields.POSITION);
            if (positionNumber != null) {
                position = positionNumber.intValue() + 1;
            }
            long alarmDate = isAlarmSet ? sSelectedDate.getTimeInMillis() : -1;
            Memo temp = new Memo(id, memoText, position, memoColor, alarmDate, isAlarmSet, false, true);
            realm1.insertOrUpdate(temp);
        }, () -> dataSavedSubject.onNext(true));
    }


    private void updateMemo(String memoText, String memoColor) {
        realm.executeTransactionAsync(realm1 -> {
            Memo toUpdate = realm1.
                    where(Memo.class)
                    .equalTo(MemoRealmFields.ID, mMemoId)
                    .findFirst();
            if (toUpdate != null) {
                toUpdate.setMemoText(memoText);
                toUpdate.setColor(memoColor);
                if (isAlarmSet || wasAlarmSet) {
                    toUpdate.setAlarmDate(sSelectedDate.getTimeInMillis());
                } else {
                    toUpdate.setAlarmDate(-1);
                }
                toUpdate.setAlarm(isAlarmSet || wasAlarmSet);
                realm1.insertOrUpdate(toUpdate);
            }
        }, () -> dataSavedSubject.onNext(true));
    }

    @Override
    public void setIsAlarmSet(boolean isSet) {
        isAlarmSet = isSet;
    }

    @Override
    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    @Override
    public void setWasAlarmSet(boolean isSet) {
        wasAlarmSet = isSet;
    }

    @Override
    public Calendar getSelectedDate() {
        return sSelectedDate;
    }

    @Override
    public void setDateSelected(Date dateClicked) {
        sSelectedDate.setTime(dateClicked);
    }

    @Override
    public int getSelectedHour() {
        return sSelectedDate.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public int getSelectedMinute() {
        return sSelectedDate.get(Calendar.MINUTE);
    }
}
