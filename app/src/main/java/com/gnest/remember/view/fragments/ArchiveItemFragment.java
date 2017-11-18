package com.gnest.remember.view.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Has to be empty, so it won't inflate its parents menu layout here
    }
}
