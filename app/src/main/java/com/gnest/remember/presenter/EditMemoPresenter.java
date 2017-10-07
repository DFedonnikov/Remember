package com.gnest.remember.presenter;

import com.gnest.remember.model.EditMemoModelImpl;
import com.gnest.remember.model.IEditMemoModel;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class EditMemoPresenter extends MvpBasePresenter<IEditMemoView> implements IEditMemoPresenter {

    private SimpleDateFormat mCalendarDateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private SimpleDateFormat mCalendarAlarmSetFormat = new SimpleDateFormat("d MMMM yyyy hh:mm", Locale.ENGLISH);

    private IEditMemoModel mModel;
    private List<Subscription> subscriptions;
    private boolean isCalendarExpanded;

    public EditMemoPresenter(int memoId) {
        this.mModel = new EditMemoModelImpl(memoId);
        this.isCalendarExpanded = false;
        subscriptions = new ArrayList<>();
    }

    @Override
    public void attachView(IEditMemoView view) {
        mModel.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        mModel.closeDB();
        tryToUnsubscribe(subscriptions);
        super.detachView(retainInstance);
    }

    private void tryToUnsubscribe(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (isSubscribed(subscription)) {
                subscription.unsubscribe();
            }
        }
    }

    private boolean isSubscribed(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }

    @Override
    public void loadData() {
        Memo memo = mModel.getData();
        if (isViewAttached()) {
            getView().setData(memo.getMemoText(), memo.getColor(), memo.isAlarmSet());
            Calendar alarmDate = Calendar.getInstance();
            if (memo.getAlarmDate() != -1) {
                alarmDate.setTimeInMillis(memo.getAlarmDate());
            }
            processSetCurrentDate(alarmDate);
        }
    }

    @Override
    public void processSetCurrentDate(Calendar date) {
        if (isViewAttached()) {
            String currentDate = mCalendarDateFormat.format(date.getTime());
            getView().setSubtitle(currentDate);
            getView().setCurrentDate(date.getTime());
        }
    }

    @Override
    public void processBackButtonPress(String memoText, String memoColor, String alarmSetText) {
        Subscription saveMemoSubscription = mModel.saveMemoToDB(memoText, memoColor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(idPositionPair -> {
                    if (mModel.isAlarmSet()) {
                        setAlarm(memoText, idPositionPair.first, alarmSetText);
                    }
                    if (isViewAttached()) {
                        getView().memoSavedInteraction(idPositionPair.second);
                    }
                });
        subscriptions.add(saveMemoSubscription);
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
            Memo memo = mModel.getEditedMemo();
            if (memo != null) {
                setNotification(false, null, memo.getId());
            }
            getView().showAlarmToast(removeAlarmMessage);
        }
    }

    private void setNotification(boolean isSet, String notificationText, int id) {
        if (isViewAttached()) {
            if (notificationText != null) {
                if (notificationText.length() > 10) {
                    notificationText = notificationText.substring(0, 10).concat("...");
                }
            }
            getView().setAlarm(isSet, mModel.getSelectedDate().getTimeInMillis(), notificationText, id);
        }
    }

    @Override
    public void processDayClicked(Date dateClicked) {
        if (isViewAttached()) {
            mModel.setDateSelected(dateClicked);
            getView().setSubtitle(mCalendarDateFormat.format(dateClicked));
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
            if (isViewAttached()) {
                getView().setAlarmVisibility(true);
            }
        }
    }

}
