package com.gnest.remember.presenter;

import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import java.util.Calendar;
import java.util.Date;

public interface IEditMemoPresenter extends MvpPresenter<IEditMemoView> {
    void loadData();

    void processSetCurrentDate(Calendar instance);

    void processRemoveAlarm(String removeAlarmMessage);

    void processDayClicked(Date dateClicked);

    void processMonthScroll(Date firstDayOfNewMonth);

    void processDatePicker();

    void processTimeSet(int hour, int minute);

    void processSaveMemo(String memoText, String memoColor, String alarmSetText, boolean isTriggeredByDrawerItem);
}
