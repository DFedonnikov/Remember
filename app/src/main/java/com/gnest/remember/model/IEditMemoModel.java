package com.gnest.remember.model;

import com.gnest.remember.model.db.data.Memo;

import java.util.Calendar;
import java.util.Date;

public interface IEditMemoModel {
    Memo getData();

    void saveMemoToDB(String memoText, String memoColor);

    void setIsAlarmSet(boolean isSet);

    boolean isAlarmSet();

    void setIsAlarmPreviouslySet(boolean isSet);

    Memo getEditedMemo();

    int getId();

    int getPosition();

    boolean isNew();

    void setDateSelected(Date dateClicked);

    int getSelectedHour();

    int getSelectedMinute();

    Calendar getSelectedDate();

    void openDB();

    void closeDB();
}
