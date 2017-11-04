package com.gnest.remember.view.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.view.fragments.EditMemoFragment;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.model.services.AlarmService;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


public class MainActivity extends AppCompatActivity implements
        EditMemoFragment.OnEditMemoFragmentInteractionListener,
        ListItemFragment.OnListItemFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static final int LM_HORIZONTAL_ORIENTATION = 0;
    public static final int LM_VERTICAL_ORIENTATION = 1;
    public static final String LM_SCROLL_ORIENTATION_KEY = "LayoutManagerOrientationKey";
    public static final String POSITION_KEY = "POSITION_KEY";
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String EXPANDED_KEY = "EXPANDED_KEY";

    private static final String EDIT_FRAG_VISIBILITY_KEY = "edit_frag_visibility_key";
    private static final String EDIT_FRAMENT_NAME = "edit_fragment";
    private static final String ITEM_FRAMENT_NAME = "item_fragment";
    public static final int ITEM_MARGINS_DP = 12;
    public static final int MAX_MEMO_SIZE_DP = 180;

    private ListItemFragment itemFragment;
    private EditMemoFragment editMemoFragment;
    private DrawerLayout drawerLayout;
    private boolean isEditFragVisible;
    private static int COLUMNS;
    private static int MEMO_SIZE_PX;
    private static int MARGINS_PX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculateColumnsAndMemoSize();
        if (savedInstanceState != null) {
            isEditFragVisible = savedInstanceState.getBoolean(EDIT_FRAG_VISIBILITY_KEY);
            FragmentManager manager = getSupportFragmentManager();
            if (isEditFragVisible) {
                editMemoFragment = (EditMemoFragment) manager.getFragment(savedInstanceState, EDIT_FRAMENT_NAME);
            } else {
                itemFragment = (ListItemFragment) manager.getFragment(savedInstanceState, ITEM_FRAMENT_NAME);
                Bundle args = itemFragment.getArguments();
                args.putInt(ListItemFragment.ARG_COLUMN_COUNT, COLUMNS);
                args.putInt(ListItemFragment.ARG_MEMO_SIZE, MEMO_SIZE_PX);
                args.putInt(ListItemFragment.ARG_MEMO_MARGINS, MARGINS_PX);
            }
        } else {
            insertItemFragment(null);
        }
    }

    private void calculateColumnsAndMemoSize() {
        int screenWidthDP = getResources().getConfiguration().screenWidthDp;
        COLUMNS = screenWidthDP / MAX_MEMO_SIZE_DP;
        float density = getResources().getDisplayMetrics().density;
        MEMO_SIZE_PX = (int) (((screenWidthDP - 2 * ITEM_MARGINS_DP * COLUMNS) / COLUMNS) * density + 0.5);
        MARGINS_PX = (int) (ITEM_MARGINS_DP * density + 0.5);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //To be executed if activity launched from notification
        Intent intent = getIntent();
        if (intent != null) {
            long id = intent.getLongExtra(AlarmService.NOTIFICATION_MEMO_ID, -1);
            if (itemFragment != null && id != -1) {
                BehaviorSubject<Boolean> dataLoadedSubject = itemFragment.getDataLodingSubject();
                BehaviorSubject<Boolean> childrenLayoutCompleteSubject = itemFragment.getLayoutManager().getChildrenLayoutCompleteSubject();

                dataLoadedSubject
                        .distinctUntilChanged(dataLoaded -> dataLoaded)
                        .zipWith(childrenLayoutCompleteSubject.distinctUntilChanged(layoutCompleted -> layoutCompleted), Pair::new)
                        .subscribeOn(Schedulers.computation())
                        .flatMap(pair -> {
                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                return Observable.from(realm.where(Memo.class)
                                        .findAllSortedAsync(MemoRealmFields.POSITION));
                            } finally {
                                if (realm != null) {
                                    realm.close();
                                }
                            }
                        })
                        .filter(clickableMemo -> clickableMemo.getId() == id)
                        .observeOn(AndroidSchedulers.mainThread())
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
        itemFragment = ListItemFragment.newInstance(COLUMNS, MEMO_SIZE_PX, MARGINS_PX);
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
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemFragment != null && itemFragment.isVisible()) {
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
    public void onEnterEditMode(int memoId) {
        Bundle bundle = new Bundle();
        bundle.putInt(EditMemoFragment.MEMO_ID_KEY, memoId);
        insertEditFragment(bundle);
    }

    @Override
    public void configureDrawer() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, findViewById(R.id.toolbar), R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_notes:
                //TODO
                Toast.makeText(this, "notes selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_item_archive:
                //TODO
                Toast.makeText(this, "archive selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_item_add:
                //TODO
                break;
            case R.id.drawer_item_share:
                //TODO
                break;
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
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
