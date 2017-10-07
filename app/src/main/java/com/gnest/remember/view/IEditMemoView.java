package com.gnest.remember.view;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.Date;

public interface IEditMemoView extends MvpView {

    void setData(String memoText, String color, boolean alarmSet);

    void setAlarm(boolean isSet, long alarmDate, String notificationText, int id);

    void showAlarmToast(String alarmMessage);

    void setSubtitle(String dateFormat);

    void setCalendarExpanded(boolean isCalendarExpanded);

    void showTimePicker(int hour, int minute);

    void setCurrentDate(Date current);

    void animateArrow(boolean isCalendarExpanded);

    void setAlarmVisibility(boolean b);

    void memoSavedInteraction(int memoPosition);
}
