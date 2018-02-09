package com.gnest.remember.view;

import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public interface IListFragmentView extends MvpView {
    void setData(RealmResults<Memo> data);

    void removeAlarm(int memoId);

    void shareMemoText(String memoText);

    void openFromNotification(long id);

    void closeNotification(int id);

    void setAlarm(int memoId, long alarmDate, String notificationText, boolean isAlarmSet, boolean isAlarmMovedToMainScreen);

    MyGridLayoutManager getLayoutManager();

    RecyclerView getRecyclerView();

    BehaviorSubject<Boolean> getDataLoadedSubject();

    ListItemFragment.OnListItemFragmentInteractionListener getInteractionListener();

    MySelectableAdapter getAdapter();

    ActionMode getActionMode();

    Observable<Boolean> showConfirmArchiveActionPopup(PublishSubject<Boolean> subject, int numOfNotes);

    Observable<Boolean> showConfirmRemovePopup(PublishSubject<Boolean> subject, int numOfNotes);
}
