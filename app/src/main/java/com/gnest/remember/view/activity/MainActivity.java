package com.gnest.remember.view.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gnest.remember.R;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.fragments.EditMemoFragment;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.model.services.AlarmService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


public class MainActivity extends AppCompatActivity implements
        EditMemoFragment.OnEditMemoFragmentInteractionListener,
        ListItemFragment.OnListItemFragmentInteractionListener {

    public static final int LM_HORIZONTAL_ORIENTATION = 0;
    public static final int LM_VERTICAL_ORIENTATION = 1;
    public static final String LM_SCROLL_ORIENTATION_KEY = "LayoutManagerOrientationKey";
    public static final String POSITION_KEY = "POSITION_KEY";
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String EXPANDED_KEY = "EXPANDED_KEY";

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
                itemFragment.setColumnCount(COLUMNS);
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
                long id = intent.getLongExtra(AlarmService.NOTIFICATION_MEMO_ID, -1);

                BehaviorSubject<Boolean> dataLoadedSubject = itemFragment.getDataLodingSubject();
                BehaviorSubject<Boolean> childrenLayoutCompleteSubject = itemFragment.getLayoutManager().getChildrenLayoutCompleteSubject();

                dataLoadedSubject
                        .distinctUntilChanged(dataLoaded -> dataLoaded)
                        .zipWith(childrenLayoutCompleteSubject.distinctUntilChanged(layoutCompleted -> layoutCompleted), Pair::new)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(pair -> Observable.from(itemFragment.getAdapter().getMemos()))
                        .takeWhile(clickableMemo -> clickableMemo.getId() == id)
                        .subscribe(clickableMemo -> {
                            itemFragment.getLayoutManager().openItem(clickableMemo.getPosition());
                            itemFragment.shutdownMemoAlarm(clickableMemo.getPosition());
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(clickableMemo.getId());
                        });
            }
        }
    }

    private void insertItemFragment(Bundle bundle) {
        itemFragment = ListItemFragment.newInstance(COLUMNS);
        if (bundle != null) {
            itemFragment.getArguments().putBundle(BUNDLE_KEY, bundle);
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
            itemFragment.onBackButtonPressed();
        } else if (editMemoFragment != null && editMemoFragment.isVisible()) {
            editMemoFragment.onBackButtonPressed();
        }
    }

    @Override
    public void onBackButtonPressed() {
        super.onBackPressed();
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
}
