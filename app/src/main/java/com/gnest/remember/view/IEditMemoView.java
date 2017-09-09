package com.gnest.remember.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IEditMemoView extends MvpView {

    AlarmManager getAlarmManager();

    PendingIntent getPendingIntent(String notificationText, int id);

    void showAlarmToast(String alarmMessage);

    void setSubtitle(String dateFormat);

    void setCalendarExpanded(boolean isCalendarExpanded);

    void showTimePicker(int hour, int minute);

    void setCurrentDate(Date current);

    void animateArrow(boolean isCalendarExpanded);

    void setAlarmVisibility(boolean b);

    void memoSavedInteraction(int memoPosition);
}
