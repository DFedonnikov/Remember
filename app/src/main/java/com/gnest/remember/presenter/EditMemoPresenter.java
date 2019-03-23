package com.gnest.remember.presenter;

import com.gnest.remember.model.EditMemoModelImpl;
import com.gnest.remember.model.IEditMemoModel;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IEditMemoView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;

public class EditMemoPresenter extends MvpBasePresenter<IEditMemoView> implements IEditMemoPresenter {

    private SimpleDateFormat mCalendarDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat mCalendarAlarmSetFormat = new SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault());

    private IEditMemoModel mModel;
    private boolean isCalendarExpanded;

    public EditMemoPresenter(int memoId) {
        this.mModel = new EditMemoModelImpl(memoId);
        this.isCalendarExpanded = false;
    }

    @Override
    public void attachView(@Nonnull IEditMemoView view) {
        super.attachView(view);
        mModel.openDB();
    }

    @Override
    public void detachView() {
        mModel.closeDB();
        super.detachView();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void loadData() {
        Memo memo = mModel.getData();
        ifViewAttached(view -> {
            Calendar alarmDate = Calendar.getInstance();
            if (memo != null) {
                view.setData(memo.getMemoText(), memo.getColor(), memo.isAlarmSet());
                if (memo.getAlarmDate() != -1) {
                    alarmDate.setTimeInMillis(memo.getAlarmDate());
                }
            }
            processSetCurrentDate(alarmDate);
        });
    }

    @Override
    public void processSetCurrentDate(Calendar date) {
        ifViewAttached(view -> {
            String currentDate = mCalendarDateFormat.format(date.getTime());
            view.setSubtitle(currentDate);
            view.setCurrentDate(date.getTime());
        });
    }

    @Override
    public void processPressBackButton() {
        ifViewAttached(view -> {
            Memo memo = mModel.getEditedMemo();
            int memoPosition = memo != null ?
                    memo.getPosition() :
                    !view.getMemoText().isEmpty() ?
                            mModel.getPosition() : -1;
            view.returnFromEdit(memoPosition);
        });
    }

    @Override
    public void saveData() {
        ifViewAttached(view -> {
            String memoText = view.getMemoText();
            String memoColor = view.getMemoColor();
            if (mModel.isAlarmSet() && !(mModel.isNew() && memoText.isEmpty())) {
                String alarmSetText = view.getAlarmSetText();
                setAlarm(view, memoText, mModel.getId(), alarmSetText);
                view.addToCalendar(mModel.getId(), getTruncatedText(memoText), mModel.getSelectedDate().getTimeInMillis());
            }
            mModel.saveMemoToDB(memoText, memoColor);
        });
    }

    private void setAlarm(IEditMemoView view, String notificationText, int id, String alarmSetText) {
        if (id != -1) {
            setNotification(view, true, notificationText, id);
            String alarmSetToast = alarmSetText +
                    " " +
                    mCalendarAlarmSetFormat.format(mModel.getSelectedDate().getTime());
            view.showAlarmToast(alarmSetToast);
        }
    }

    @Override
    public void processRemoveAlarm(String removeAlarmMessage) {
        ifViewAttached(view -> {
            mModel.setIsAlarmSet(false);
            mModel.setIsAlarmPreviouslySet(false);
            Memo memo = mModel.getEditedMemo();
            if (memo != null) {
                int id = memo.getId();
                setNotification(view, false, null, id);
                view.removeFromCalendar(id);
            }
            view.showAlarmToast(removeAlarmMessage);
        });
    }

    private void setNotification(IEditMemoView view, boolean isSet, String notificationText, int id) {
        String notificationTextLocal = getTruncatedText(notificationText);
        long timeInMillis = mModel.getSelectedDate().getTimeInMillis();
        view.setAlarm(isSet, timeInMillis, notificationTextLocal, id);
    }

    private String getTruncatedText(String notificationText) {
        if (notificationText == null) {
            return "";
        }
        if (notificationText.length() > 30) {
            return notificationText.substring(0, 30).concat("...");
        }
        return notificationText;
    }

    @Override
    public void processDayClicked(Date dateClicked) {
        ifViewAttached(view -> {
            mModel.setDateSelected(dateClicked);
            view.setSubtitle(mCalendarDateFormat.format(dateClicked));
            isCalendarExpanded = !isCalendarExpanded;
            view.setCalendarExpanded(isCalendarExpanded);
            view.showTimePicker(mModel.getSelectedHour(), mModel.getSelectedMinute());
        });
    }

    @Override
    public void processMonthScroll(Date firstDayOfNewMonth) {
        ifViewAttached(view -> view.setSubtitle(mCalendarDateFormat.format(firstDayOfNewMonth)));
    }

    @Override
    public void processDatePicker() {
        ifViewAttached(view -> {
            view.animateArrow(isCalendarExpanded);
            isCalendarExpanded = !isCalendarExpanded;
            view.setCalendarExpanded(isCalendarExpanded);
        });
    }

    @Override
    public void processTimeSet(int hour, int minute) {
        ifViewAttached(view -> {
            Calendar selectedDate = mModel.getSelectedDate();
            selectedDate.set(Calendar.HOUR_OF_DAY, hour);
            selectedDate.set(Calendar.MINUTE, minute);
            Calendar now = Calendar.getInstance();
            if (selectedDate.after(now)) {
                mModel.setIsAlarmSet(true);
                view.setAlarmVisibility(true);
            }
        });
    }

}
