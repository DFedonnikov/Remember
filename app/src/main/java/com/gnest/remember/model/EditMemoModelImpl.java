package com.gnest.remember.model;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import io.realm.Realm;

public class EditMemoModelImpl implements IEditMemoModel {

    private static Calendar sSelectedDate = Calendar.getInstance();

    private Realm mRealm;
    private int mMemoId;
    private int mMemoPosition;
    private boolean isNew;
    private boolean isAlarmPreviouslySet;
    private boolean isAlarmSet;


    public EditMemoModelImpl(int memoId) {
        this.mMemoId = memoId;
        this.mMemoPosition = -1;
        this.isAlarmSet = false;
        this.isNew = memoId == -1;
    }

    @Override
    public void openDB() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void closeDB() {
        mRealm.close();
    }

    @Override
    @Nullable
    public Memo getData() {
        Memo memo = getEditedMemo();
        if (isNew) {
            mMemoId = calculateNewId();
            mMemoPosition = calculateNewPosition();
        }
        if (memo != null) {
            mMemoId = memo.getId();
            mMemoPosition = memo.getPosition();
            isAlarmPreviouslySet = memo.isAlarmSet();
            sSelectedDate.setTimeInMillis(memo.getAlarmDate());
        }
        return memo;
    }

    @Override
    @Nullable
    public Memo getEditedMemo() {
        return isNew ? null : mRealm.where(Memo.class)
                .equalTo(MemoRealmFields.ID, mMemoId)
                .findFirst();
    }

    @Override
    public void saveMemoToDB(String memoText, String memoColor) {
        if (isNew && !memoText.isEmpty()) {
            isNew = false;
            insertNewMemo(memoText, memoColor);
        } else {
            updateMemo(memoText, memoColor);
        }
    }

    private void insertNewMemo(String memoText, String memoColor) {
        mRealm.executeTransactionAsync(realm1 -> {
            long alarmDate = isAlarmSet ? sSelectedDate.getTimeInMillis() : -1;
            Memo temp = new Memo(mMemoId, memoText, mMemoPosition, memoColor, alarmDate, isAlarmSet, false, true);
            realm1.insertOrUpdate(temp);
        });
    }

    private int calculateNewPosition() {
        int position = 0;
        Number positionNumber = mRealm.where(Memo.class)
                .max(MemoRealmFields.POSITION);
        if (positionNumber != null) {
            position = positionNumber.intValue() + 1;
        }
        return position;
    }

    private int calculateNewId() {
        int idMain = 0;
        int idArchived = 0;
        Number idNumberMain = mRealm.where(Memo.class)
                .max(MemoRealmFields.ID);
        if (idNumberMain != null) {
            idMain = idNumberMain.intValue() + 1;
        }
        try (Realm realmAchrived = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME))) {
            Number idNumberArchived = realmAchrived
                    .where(Memo.class)
                    .max(MemoRealmFields.ID);
            if (idNumberArchived != null) {
                idArchived = idNumberArchived.intValue() + 1;
            }
        }
        return Math.max(idMain, idArchived);
    }

    private void updateMemo(String memoText, String memoColor) {
        mRealm.executeTransactionAsync(realm1 -> {
            Memo toUpdate = realm1.
                    where(Memo.class)
                    .equalTo(MemoRealmFields.ID, mMemoId)
                    .findFirst();
            if (toUpdate != null) {
                toUpdate.setMemoText(memoText);
                toUpdate.setColor(memoColor);
                if (isAlarmSet || isAlarmPreviouslySet) {
                    toUpdate.setAlarmDate(sSelectedDate.getTimeInMillis());
                } else {
                    toUpdate.setAlarmDate(-1);
                }
                toUpdate.setAlarm(isAlarmSet || isAlarmPreviouslySet);
                realm1.insertOrUpdate(toUpdate);
            }
        });
    }

    @Override
    public int getId() {
        return mMemoId;
    }

    @Override
    public int getPosition() {
        return mMemoPosition;
    }

    @Override
    public boolean isNew() {
        return isNew;
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
    public void setIsAlarmPreviouslySet(boolean isSet) {
        isAlarmPreviouslySet = isSet;
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
