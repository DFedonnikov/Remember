package com.gnest.remember.presenter;

import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

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

    void processBackButtonPress(String memoText, String memoColor, String alarmSetText);
}
