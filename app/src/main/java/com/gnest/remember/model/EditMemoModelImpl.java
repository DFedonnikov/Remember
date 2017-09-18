package com.gnest.remember.model;

import android.support.v4.util.Pair;

import com.gnest.remember.App;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.data.Memo;
import com.gnest.remember.model.db.DatabaseAccess;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by DFedonnikov on 09.09.2017.
 */

public class EditMemoModelImpl implements IEditMemoModel {

    private static Calendar sSelectedDate = Calendar.getInstance();

    private DatabaseAccess mDatabaseAccess;
    private ClickableMemo mEditedMemo;
    private boolean wasAlarmSet;
    private boolean isAlarmSet;


    public EditMemoModelImpl(ClickableMemo memo) {
        this.mDatabaseAccess = DatabaseAccess.getInstance(App.self());
        this.mEditedMemo = memo;
        this.isAlarmSet = false;
        if (memo != null) {
            this.wasAlarmSet = memo.isAlarmSet();
        }
    }

    @Override
    public void openDB() {
        mDatabaseAccess.open();
    }

    @Override
    public void closeDB() {
        mDatabaseAccess.close();
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
    public Observable<Pair<Integer, Integer>> saveMemoToDB(String memoText, String memoColor) {
        if (mEditedMemo == null) {
            // Add new mMemo
            if (!memoText.isEmpty()) {
                Memo temp = new Memo(memoText, memoColor, isAlarmSet);
                return getObservableFromCallable(mDatabaseAccess.save(temp));
            } else {
                return Observable.just(new Pair<>(-1, -1));
            }
        } else {
            // Update the mMemo
            mEditedMemo.setMemoText(memoText);
            mEditedMemo.setColor(memoColor);
            mEditedMemo.setAlarm(isAlarmSet || wasAlarmSet);
            return getObservableFromCallable(mDatabaseAccess.update(mEditedMemo));
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
    public ClickableMemo getEditedMemo() {
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
