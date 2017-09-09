package com.gnest.remember.model;

import com.gnest.remember.model.data.ClickableMemo;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by DFedonnikov on 09.09.2017.
 */

public interface IEditMemoModel {
    void setIsAlarmSet(boolean isSet);

    void setWasAlarmSet(boolean isSet);

    ClickableMemo getEditedMemo();

    void setDateSelected(Date dateClicked);

    int getSelectedHour();

    int getSelectedMinute();

    Calendar getSelectedDate();

    void saveMemoToDB(String memoText, String memoColor);
}
