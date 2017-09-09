package com.gnest.remember.presenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;

import com.gnest.remember.model.EditMemoModelImpl;
import com.gnest.remember.model.IEditMemoModel;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.IEditMemoView;
import com.gnest.remember.view.activity.MainActivity;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public class EditMemoPresenter extends MvpBasePresenter<IEditMemoView> implements IEditMemoPresenter {

    private SimpleDateFormat mCalendarDateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private SimpleDateFormat mCalendarAlarmSetFormat = new SimpleDateFormat("d MMMM yyyy hh:mm", Locale.ENGLISH);

    private IEditMemoModel mModel;
    private boolean isCalendarExpanded;

    public EditMemoPresenter(ClickableMemo memo) {
        this.mModel = new EditMemoModelImpl(memo);
        this.isCalendarExpanded = false;
    }

    @Override
    public void attachView(IEditMemoView view) {
        super.attachView(view);

    }

    @Override
    public void processBackButtonPress(String memoText, String memoColor, String alarmSetText) {
        mModel.saveMemoToDB(memoText, memoColor);
        int position = -1;
        if (mModel.getEditedMemo() != null) {
            if (mModel.getEditedMemo().isAlarmSet()) {
                setAlarm(memoText, mModel.getEditedMemo().getId(), alarmSetText);
            }
            position = mModel.getEditedMemo().getPosition();
        }
        if (isViewAttached()) {
            getView().memoSavedInteraction(position);
        }
    }

    private void setAlarm(String notificationText, int id, String alarmSetText) {
        if (id != -1) {
            setNotification(true, notificationText, id);
            if (isViewAttached()) {
                StringBuilder alarmSetToast = new StringBuilder();
                alarmSetToast
                        .append(alarmSetText)
                        .append(" ")
                        .append(mCalendarAlarmSetFormat.format(mModel.getSelectedDate().getTime()));
                getView().showAlarmToast(alarmSetToast.toString());
            }
        }
    }

    @Override
    public void processRemoveAlarm(String removeAlarmMessage) {
        if (isViewAttached()) {
            mModel.setIsAlarmSet(false);
            mModel.setWasAlarmSet(false);
            if (mModel.getEditedMemo() != null) {
                setNotification(false, null, mModel.getEditedMemo().getId());
            }
            getView().showAlarmToast(removeAlarmMessage);
        }
    }

    private void setNotification(boolean isSet, String notificationText, int id) {
        AlarmManager manager = getView().getAlarmManager();
        if (notificationText != null) {
            if (notificationText.length() > 10) {
                notificationText = notificationText.substring(0, 10).concat("...");
            }
        }
        PendingIntent pendingIntent = getView().getPendingIntent(notificationText, id);

        if (isSet) {
            manager.set(AlarmManager.RTC_WAKEUP, mModel.getSelectedDate().getTimeInMillis(), pendingIntent);
        } else {
            manager.cancel(pendingIntent);
        }
    }

    @Override
    public void processDayClicked(Date dateClicked) {
        if (isViewAttached()) {
            mModel.setDateSelected(dateClicked);
            getView().setSubtitle(mCalendarDateFormat.format(dateClicked));
//            selectedDateFormatted = mCalendarDateFormat.format(dateClicked);
            isCalendarExpanded = !isCalendarExpanded;
            getView().setCalendarExpanded(isCalendarExpanded);
            getView().showTimePicker(mModel.getSelectedHour(), mModel.getSelectedMinute());
        }
    }

    @Override
    public void processMonthScroll(Date firstDayOfNewMonth) {
        if (isViewAttached()) {
            getView().setSubtitle(mCalendarDateFormat.format(firstDayOfNewMonth));
        }
    }

    @Override
    public void processSetCurrentDate() {
        Date current = Calendar.getInstance().getTime();
        mModel.setDateSelected(current);
        String currentDate = mCalendarDateFormat.format(current);
        if (isViewAttached()) {
            getView().setSubtitle(currentDate);
            getView().setCurrentDate(current);
        }
    }

    @Override
    public void processDatePicker() {
        if (isViewAttached()) {
            getView().animateArrow(isCalendarExpanded);
            isCalendarExpanded = !isCalendarExpanded;
            getView().setCalendarExpanded(isCalendarExpanded);
        }
    }

    @Override
    public void processTimeSet(int hour, int minute) {
        Calendar selectedDate = mModel.getSelectedDate();
        selectedDate.set(Calendar.HOUR_OF_DAY, hour);
        selectedDate.set(Calendar.MINUTE, minute);
        Calendar now = Calendar.getInstance();
        if (selectedDate.after(now)) {
            mModel.setIsAlarmSet(true);
            getView().setAlarmVisibility(true);
        }
    }

}
