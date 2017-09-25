package com.gnest.remember.model;

import android.support.v4.util.Pair;

import com.gnest.remember.model.db.data.Memo;

import java.util.Calendar;
import java.util.Date;

import rx.Observable;

/**
 * Created by DFedonnikov on 09.09.2017.
 */

public interface IEditMemoModel {
    Memo getData();

    Observable<Pair<Integer, Integer>> saveMemoToDB(String memoText, String memoColor);

    void setIsAlarmSet(boolean isSet);

    boolean isAlarmSet();

    void setWasAlarmSet(boolean isSet);

    Memo getEditedMemo();

    void setDateSelected(Date dateClicked);

    int getSelectedHour();

    int getSelectedMinute();

    Calendar getSelectedDate();

    void openDB();

    void closeDB();
}
