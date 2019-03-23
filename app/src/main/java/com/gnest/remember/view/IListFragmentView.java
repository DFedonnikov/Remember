package com.gnest.remember.view;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
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

    ListItemFragment.OnListItemFragmentInteractionListener getInteractionListener();

    MySelectableAdapter getAdapter();

    ActionMode getActionMode();

    Snackbar getArchiveSnackbar(int numOfNotes);

    Snackbar getDeleteSnackbar(int numOfNotes);

    void removeFromCalendar(int id);
}