package com.gnest.remember.model;

import android.support.v4.util.Pair;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.DatabaseAccess;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by DFedonnikov on 09.09.2017.
 */

public class EditMemoModelImpl implements IEditMemoModel {

    private static Calendar sSelectedDate = Calendar.getInstance();
    private final BehaviorSubject<Boolean> dataSavedSubject = BehaviorSubject.create();

    private Realm realm;
    private int mMemoId;

    private DatabaseAccess mDatabaseAccess;
    private volatile Memo mEditedMemo;
    private boolean wasAlarmSet;
    private boolean isAlarmSet;


    public EditMemoModelImpl(int memoId) {
//        this.mDatabaseAccess = DatabaseAccess.getInstance(App.self());
//        openDB();
        this.mMemoId = memoId;
        this.isAlarmSet = false;
//        if (memo != null) {
//            this.wasAlarmSet = memo.isAlarmSet();
//        }
    }

    @Override
    public void openDB() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void closeDB() {
        realm.close();
    }

    private <T> Observable<T> getObservableFromCallable(Callable<T> callable) {
        return Observable
                .fromCallable(callable)
                .subscribeOn(Schedulers.computation())
                .retry((integer, throwable) -> {
                    if (throwable instanceof IllegalStateException) {
                        openDB();
                        return true;
                    } else {
                        return false;
                    }
                });
    }

    @Override
    public Observable<Memo> getData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            mEditedMemo = realm.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, mMemoId)
                    .findFirstAsync();
            return mEditedMemo.asObservable();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public Observable<Pair<Integer, Integer>> saveMemoToDB(String memoText, String memoColor) {
        if (mEditedMemo == null) {
            if (memoText.isEmpty()) {
                return Observable.just(new Pair<>(-1, -1));
            }
            insertNewMemo(memoText, memoColor);
        } else {
            updateMemo(memoText, memoColor);
        }
        return mEditedMemo
                .asObservable()
                .flatMap(new Func1<RealmObject, Observable<Pair<Integer, Integer>>>() {
                    @Override
                    public Observable<Pair<Integer, Integer>> call(RealmObject realmObject) {
                        Memo obj = (Memo) realmObject;
                        return Observable.just(new Pair<>(obj.getId(), obj.getPosition()));
                    }
                });


//        return dataSavedSubject
//                .subscribeOn(Schedulers.computation())
//                .distinctUntilChanged()
//                .zipWith(Observable
//                        .just(mEditedMemo), new Func2<Boolean, Memo, Pair<Integer, Integer>>() {
//                    @Override
//                    public Pair<Integer, Integer> call(Boolean aBoolean, Memo memo) {
//                        return new Pair<>(mEditedMemo.getId(), mEditedMemo.getPosition());
//                    }
//                })
//                .doOnUnsubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        dataSavedSubject.onNext(false);
//                    }
//                });
    }

    private void insertNewMemo(String memoText, String memoColor) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                int id = 0;
                int position = 0;

                Number idNumber = realm1.where(Memo.class)
                        .max(MemoRealmFields.ID);
                if (idNumber != null) {
                    id = idNumber.intValue() + 1;
                }
                Number positionNumber = realm1.where(Memo.class)
                        .max(MemoRealmFields.POSITION);
                if (positionNumber != null) {
                    position = positionNumber.intValue() + 1;
                }
                Memo temp = new Memo(id, memoText, position, memoColor, isAlarmSet, false, true);
                realm1.insertOrUpdate(temp);
                mEditedMemo = realm1.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, id)
                        .findFirst();
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void updateMemo(String memoText, String memoColor) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                mEditedMemo.setMemoText(memoText);
                mEditedMemo.setColor(memoColor);
                mEditedMemo.setAlarm(isAlarmSet || wasAlarmSet);
                realm1.insertOrUpdate(mEditedMemo);
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
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
    public Memo getEditedMemo() {
        return mEditedMemo;
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
