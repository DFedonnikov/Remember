package com.gnest.remember.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.presenter.ArchiveFragmentPresenter;
import com.gnest.remember.presenter.IListFragmentPresenter;

public class ArchiveItemFragment extends ListItemFragment {


    public static ArchiveItemFragment newInstance(int columnCount, int memoSize, int margins) {
        ArchiveItemFragment fragment = new ArchiveItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_MEMO_SIZE, memoSize);
        args.putInt(ARG_MEMO_MARGINS, margins);
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
    void setUpCancelMessage(View layout) {
        TextView cancelMessage = layout.findViewById(R.id.cancelText);
        cancelMessage.setText(R.string.note_deleted_message);
    }
}
