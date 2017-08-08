package com.gnest.remember.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gnest.remember.R;
import com.gnest.remember.asynctasks.OpenMemoFromNotificationTask;
import com.gnest.remember.data.ClickableMemo;
import com.gnest.remember.layout.EditMemoFragment;
import com.gnest.remember.layout.ListItemFragment;
import com.gnest.remember.services.AlarmService;


public class MainActivity extends AppCompatActivity implements
        EditMemoFragment.OnEditMemoFragmentInteractionListener,
        ListItemFragment.OnItemListFragmentInteractionListener,
        OpenMemoFromNotificationTask.NotificationOpenerHelper {
    private static final String EDIT_FRAG_VISIBILITY_KEY = "edit_frag_visibility_key";
    private static final String EDIT_FRAMENT_NAME = "edit_fragment";
    private static final String ITEM_FRAMENT_NAME = "item_fragment";
    private static final int MEMO_WIDTH = 175;
    private ListItemFragment itemFragment;
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
                itemFragment = (ListItemFragment) manager.getFragment(savedInstanceState, ITEM_FRAMENT_NAME);
                itemFragment.setmColumnCount(COLUMNS);
            }
        } else {
            insertItemFragment(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //To be executed if activity launched from notification
        Intent intent = getIntent();
        if (intent != null) {
            if (itemFragment != null) {
                OpenMemoFromNotificationTask task = new OpenMemoFromNotificationTask(this, itemFragment.getAdapter());
                task.execute(intent.getLongExtra(AlarmService.NOTIFICATION_MEMO_ID, -1));
            }
        }

    }

    private void insertItemFragment(Bundle bundle) {
        itemFragment = ListItemFragment.newInstance(COLUMNS);
        if (bundle != null) {
            itemFragment.getArguments().putBundle(ListItemFragment.BUNDLE_KEY, bundle);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.current_fragment, itemFragment, null);
        ft.commit();
    }

    @Override
    public void onSaveEditMemoFragmentInteraction(Bundle bundle) {
        insertItemFragment(bundle);
    }

    @Override
    public void onBackPressed() {
        if (itemFragment != null && itemFragment.isVisible()) {
//            super.onBackPressed();
            itemFragment.onBackButtonPressed();
        } else if (editMemoFragment.isVisible()) {
            Bundle bundle = null;
            Bundle editMemoFragmentBinder = editMemoFragment.getArguments();
            if (editMemoFragmentBinder != null) {
                bundle = new Bundle();
                bundle.putInt(ListItemFragment.LM_SCROLL_ORIENTATION_KEY, ListItemFragment.LM_HORIZONTAL_ORIENTATION);
                bundle.putInt(ListItemFragment.POSITION_KEY, ((ClickableMemo) editMemoFragment.getArguments().getBinder(EditMemoFragment.MEMO_KEY)).getPosition());
                bundle.putBoolean(ListItemFragment.EXPANDED_KEY, true);
            }
            insertItemFragment(bundle);
        }
    }


    @Override
    public void onAddButtonPressed() {
        insertEditFragment(null);
    }

    @Override
    public void onEnterEditMode(ClickableMemo memo) {
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

    @Override
    public void openMemoFromNotification(int position, int notificationId) {
        itemFragment.openClickedItem(position);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}
