package com.gnest.remember.presenter;

import com.gnest.remember.model.EditMemoModelImpl;
import com.gnest.remember.model.IEditMemoModel;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class EditMemoPresenter extends MvpBasePresenter<IEditMemoView> implements IEditMemoPresenter {

    private SimpleDateFormat mCalendarDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat mCalendarAlarmSetFormat = new SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault());

    private IEditMemoModel mModel;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private boolean isCalendarExpanded;

    public EditMemoPresenter(int memoId) {
        this.mModel = new EditMemoModelImpl(memoId);
        this.isCalendarExpanded = false;
    }

    @Override
    public void attachView(IEditMemoView view) {
        mModel.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        mModel.closeDB();
        mSubscriptions.unsubscribe();
        super.detachView(retainInstance);
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
            IEditMemoView view = getView();
            String currentDate = mCalendarDateFormat.format(date.getTime());
            view.setSubtitle(currentDate);
            view.setCurrentDate(date.getTime());
        }
    }

    @Override
    public void processSaveMemo(String memoText, String memoColor, String alarmSetText, boolean isTriggeredByDrawerItem) {
        Subscription saveMemoSubscription = mModel.saveMemoToDB(memoText, memoColor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(idPositionPair -> {
                    if (mModel.isAlarmSet() && idPositionPair.first != null) {
                        setAlarm(memoText, idPositionPair.first, alarmSetText);
                    }
                    if (isViewAttached() && idPositionPair.second != null) {
                        getView().memoSavedInteraction(idPositionPair.second, isTriggeredByDrawerItem);
                    }
                });
        mSubscriptions.add(saveMemoSubscription);
    }

    private void setAlarm(String notificationText, int id, String alarmSetText) {
        if (id != -1) {
            setNotification(true, notificationText, id);
            if (isViewAttached()) {
                String alarmSetToast = alarmSetText +
                        " " +
                        mCalendarAlarmSetFormat.format(mModel.getSelectedDate().getTime());
                getView().showAlarmToast(alarmSetToast);
            }
        }
    }

    @Override
    public void processRemoveAlarm(String removeAlarmMessage) {
        if (isViewAttached()) {
            mModel.setIsAlarmSet(false);
            mModel.setIsAlarmPreviouslySet(false);
            Memo memo = mModel.getEditedMemo();
            if (memo != null) {
                setNotification(false, null, memo.getId());
            }
            getView().showAlarmToast(removeAlarmMessage);
        }
    }

    private void setNotification(boolean isSet, String notificationText, int id) {
        if (isViewAttached()) {
            String notificationTextLocal = notificationText;
            if (notificationTextLocal != null && notificationTextLocal.length() > 10) {
                    notificationTextLocal = notificationText.substring(0, 10).concat("...");
            }
            getView().setAlarm(isSet, mModel.getSelectedDate().getTimeInMillis(), notificationTextLocal, id);
        }
    }

    @Override
    public void processDayClicked(Date dateClicked) {
        if (isViewAttached()) {
            mModel.setDateSelected(dateClicked);
            IEditMemoView view = getView();
            view.setSubtitle(mCalendarDateFormat.format(dateClicked));
            isCalendarExpanded = !isCalendarExpanded;
            view.setCalendarExpanded(isCalendarExpanded);
            view.showTimePicker(mModel.getSelectedHour(), mModel.getSelectedMinute());
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
            IEditMemoView view = getView();
            view.animateArrow(isCalendarExpanded);
            isCalendarExpanded = !isCalendarExpanded;
            view.setCalendarExpanded(isCalendarExpanded);
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
