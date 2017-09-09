package com.gnest.remember.presenter;

import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import java.util.Date;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IEditMemoPresenter extends MvpPresenter<IEditMemoView> {
    void processRemoveAlarm(String removeAlarmMessage);

    void processDayClicked(Date dateClicked);

    void processMonthScroll(Date firstDayOfNewMonth);

    void processSetCurrentDate();

    void processDatePicker();

    void processTimeSet(int hour, int minute);

    void processBackButtonPress(String memoText, String memoColor, String alarmSetText);
}
