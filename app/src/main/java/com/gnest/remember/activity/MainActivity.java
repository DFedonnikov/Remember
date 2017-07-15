package com.gnest.remember.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gnest.remember.R;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.layout.EditMemoFragment;
import com.gnest.remember.layout.ItemFragment;


public class MainActivity extends AppCompatActivity implements EditMemoFragment.OnEditMemoFragmentInteractionListener, ItemFragment.OnItemListFragmentInteractionListener {
    private static final String EDIT_FRAG_VISIBILITY_KEY = "edit_frag_visibility_key";
    private static final String EDIT_FRAMENT_NAME = "edit_fragment";
    private static final String ITEM_FRAMENT_NAME = "item_fragment";
    private static final int MEMO_WIDTH = 175;
    private ItemFragment itemFragment;
    private EditMemoFragment editMemoFragment;
    private boolean isEditFragVisible;
    private static int COLUMNS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screenWidthDP = getResources().getConfiguration().screenWidthDp;
        COLUMNS = screenWidthDP / MEMO_WIDTH;
        if (savedInstanceState != null) {
            isEditFragVisible = savedInstanceState.getBoolean(EDIT_FRAG_VISIBILITY_KEY);
            FragmentManager manager = getSupportFragmentManager();
            if (isEditFragVisible) {
                editMemoFragment = (EditMemoFragment) manager.getFragment(savedInstanceState, EDIT_FRAMENT_NAME);
            } else {
                itemFragment = (ItemFragment) manager.getFragment(savedInstanceState, ITEM_FRAMENT_NAME);
                itemFragment.setmColumnCount(COLUMNS);
            }
        } else {
            insertItemFragment();
        }

    }

    private void insertItemFragment() {
        itemFragment = ItemFragment.newInstance(COLUMNS);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.current_fragment, itemFragment, null);
        ft.commit();
    }

    @Override
    public void onSaveEditMemoFragmentInteraction() {
        insertItemFragment();
    }

    @Override
    public void onBackPressed() {
        if (itemFragment != null && itemFragment.isVisible()) {
            super.onBackPressed();
        } else if (editMemoFragment.isVisible()) {
            insertItemFragment();
        }
    }


    @Override
    public void onAddButtonPressed() {
        insertEditFragment(null);
    }

    @Override
    public void onEditButtonPressed(SelectableMemo memo) {
        Bundle bundle = new Bundle();
        bundle.putBinder(EditMemoFragment.MEMO_KEY, memo);
        insertEditFragment(bundle);
    }

    private void insertEditFragment(Bundle state) {
        editMemoFragment = EditMemoFragment.newInstance();
        if (state != null) {
            editMemoFragment.setArguments(state);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.current_fragment, editMemoFragment, null);
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isEditFragmentVisible = editMemoFragment != null && editMemoFragment.isVisible();
        FragmentManager manager = getSupportFragmentManager();
        if (isEditFragmentVisible) {
            manager.putFragment(outState, EDIT_FRAMENT_NAME, editMemoFragment);
        } else {
            manager.putFragment(outState, ITEM_FRAMENT_NAME, itemFragment);
        }
        outState.putBoolean(EDIT_FRAG_VISIBILITY_KEY, isEditFragmentVisible);
    }
}
