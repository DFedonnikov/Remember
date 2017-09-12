package com.gnest.remember.model;

import com.gnest.remember.App;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.data.Memo;
import com.gnest.remember.model.db.DatabaseAccess;

import java.util.Calendar;
import java.util.Date;

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
    public void setIsAlarmSet(boolean isSet) {
        isAlarmSet = isSet;
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

    @Override
    public void saveMemoToDB(String memoText, String memoColor) {
        mDatabaseAccess.open();
        if (mEditedMemo == null) {
            // Add new mMemo
            if (!memoText.isEmpty()) {
                Memo temp = new Memo(memoText, memoColor, isAlarmSet);
                mDatabaseAccess.save(temp);
            }
        } else {
            // Update the mMemo
            mEditedMemo.setMemoText(memoText);
            mEditedMemo.setColor(memoColor);
            mEditedMemo.setAlarm(isAlarmSet || wasAlarmSet);
            mDatabaseAccess.update(mEditedMemo);
        }
        mDatabaseAccess.close();
    }
}
