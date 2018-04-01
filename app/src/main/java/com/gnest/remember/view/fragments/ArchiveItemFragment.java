package com.gnest.remember.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;

import com.gnest.remember.R;
import com.gnest.remember.presenter.ArchiveFragmentPresenter;
import com.gnest.remember.presenter.IListFragmentPresenter;

import butterknife.BindString;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArchiveItemFragment extends ListItemFragment {

    @BindString(R.string.note_unarchived_message)
    String noteUnarchiveMessage;
    @BindString(R.string.note_unarchived_message_1)
    String noteUnarchiveMessage1;
    @BindString(R.string.note_unarchived_message_2)
    String noteUnarchiveMessage2;


    public static ArchiveItemFragment newInstance(int columnCount, int memoSize, int margins) {
        ArchiveItemFragment fragment = new ArchiveItemFragment();
        Bundle args = new Bundle();
        args.putInt(ListItemFragment.ARG_COLUMN_COUNT, columnCount);
        args.putInt(ListItemFragment.ARG_MEMO_SIZE, memoSize);
        args.putInt(ListItemFragment.ARG_MEMO_MARGINS, margins);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public IListFragmentPresenter createPresenter() {
        return new ArchiveFragmentPresenter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Has to be empty, so it won't inflate its parents menu layout here
    }

    @Override
    String getArchiveActionPluralForm(int plural) {
        switch (plural) {
            case 2:
                return noteUnarchiveMessage2;
            case 1:
                return noteUnarchiveMessage1;
            default:
                return noteUnarchiveMessage;
        }
    }

    public void waitForLoadAndOpenFromNotification(int id) {
        getDataLoadedSubject()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(loaded -> openFromNotification(id));
    }
}
