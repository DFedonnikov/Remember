package com.gnest.remember.ui.view;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.ui.adapters.MySelectableAdapter;
import com.gnest.remember.ui.layoutmanagers.MyGridLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import androidx.appcompat.view.ActionMode;
import io.realm.RealmResults;

public interface IListFragmentView extends MvpView {
    void setData(RealmResults<Memo> data);

    void removeAlarm(int memoId);

    void shareMemoText(String memoText);

    void openFromNotification(int id);

    void closeNotification(int id);

    boolean isNotificationVisible(int id);

    void setAlarm(int memoId, long alarmDate, String notificationText, boolean isAlarmSet, boolean isAlarmMovedToMainScreen);

    MyGridLayoutManager getLayoutManager();

    MySelectableAdapter getAdapter();

    ActionMode getActionMode();

    Snackbar getArchiveSnackbar(int numOfNotes);

    Snackbar getDeleteSnackbar(int numOfNotes);

    void removeFromCalendar(int id);
}