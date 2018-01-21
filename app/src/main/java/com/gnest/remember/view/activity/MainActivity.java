package com.gnest.remember.view.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.view.fragments.ArchiveItemFragment;
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

    private static final String EDIT_FRAGMENT_NAME = "Edit";
    private static final String ARCHIVE_FRAGMENT_NAME = "Archive";
    private static final String ITEM_FRAGMENT_NAME = "Notes";
    private static final String EDIT_FRAG_VISIBILITY_KEY = "Edit frag visibility key";
    private static final String ARCHIVE_FRAG_VISIBILITY_KEY = "Archive frag visibility key";
    public static final int ITEM_MARGINS_DP = 12;
    public static final int MAX_MEMO_SIZE_DP = 180;

    private ListItemFragment itemFragment;
    private EditMemoFragment editMemoFragment;
    private ArchiveItemFragment archiveFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static int COLUMNS;
    private static int MEMO_SIZE_PX;
    private static int MARGINS_PX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculateColumnsAndMemoSize();
        configureDrawer();
        if (savedInstanceState != null) {
            boolean isEditFragVisible = savedInstanceState.getBoolean(EDIT_FRAG_VISIBILITY_KEY);
            boolean isArchiveFragVisible = savedInstanceState.getBoolean(ARCHIVE_FRAG_VISIBILITY_KEY);
            FragmentManager manager = getSupportFragmentManager();
            if (isEditFragVisible) {
                editMemoFragment = (EditMemoFragment) manager.getFragment(savedInstanceState, EDIT_FRAGMENT_NAME);
            } else if (isArchiveFragVisible) {
                archiveFragment = (ArchiveItemFragment) manager.getFragment(savedInstanceState, ARCHIVE_FRAGMENT_NAME);
                setDimenArgs(archiveFragment);
            } else {
                itemFragment = (ListItemFragment) manager.getFragment(savedInstanceState, ITEM_FRAGMENT_NAME);
                setDimenArgs(itemFragment);
            }
        } else {
            insertItemFragment(null);
        }
    }

    private <T extends Fragment> void setDimenArgs(T fragment) {
        Bundle args = fragment.getArguments();
        args.putInt(ListItemFragment.ARG_COLUMN_COUNT, COLUMNS);
        args.putInt(ListItemFragment.ARG_MEMO_SIZE, MEMO_SIZE_PX);
        args.putInt(ListItemFragment.ARG_MEMO_MARGINS, MARGINS_PX);
    }

    private void calculateColumnsAndMemoSize() {
        int screenWidthDP = getResources().getConfiguration().screenWidthDp;
        COLUMNS = screenWidthDP / MAX_MEMO_SIZE_DP;
        float density = getResources().getDisplayMetrics().density;
        MEMO_SIZE_PX = (int) (((screenWidthDP - 2 * ITEM_MARGINS_DP * COLUMNS) / COLUMNS) * density + 0.5);
        MARGINS_PX = (int) (ITEM_MARGINS_DP * density + 0.5);
    }

    private void configureDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, findViewById(R.id.toolbar), R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                            if (notificationManager != null) {
                                notificationManager.cancel(clickableMemo.getId());
                            }
                        });
            }
        }
    }

    private void insertItemFragment(Bundle bundle) {
        insertFragment(ListItemFragment.class, bundle);
        setTitle(getString(R.string.notes));
    }

    private void insertArchiveFragment() {
        insertFragment(ArchiveItemFragment.class, null);
        setTitle(R.string.archive);
    }

    private void insertEditFragment(Bundle state) {
        insertFragment(EditMemoFragment.class, state);
    }

    private <T extends Class<? extends Fragment>> void insertFragment(T fragmentClass, Bundle bundle) {
        Fragment fragment;
        if (fragmentClass.equals(ArchiveItemFragment.class)) {
            fragment = archiveFragment = ArchiveItemFragment.newInstance(COLUMNS, MEMO_SIZE_PX, MARGINS_PX);
        } else if (fragmentClass.equals(EditMemoFragment.class)) {
            fragment = editMemoFragment = EditMemoFragment.newInstance();
            if (bundle != null) {
                editMemoFragment.setArguments(bundle);
            }
        } else {
            fragment = itemFragment = ListItemFragment.newInstance(COLUMNS, MEMO_SIZE_PX, MARGINS_PX);
            if (bundle != null) {
                itemFragment.getArguments().putBundle(ListItemFragment.BUNDLE_KEY, bundle);
            }
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.current_fragment, fragment, null);
        ft.commit();
    }

    @Override
    public void onSaveEditMemoFragmentInteraction(Bundle bundle, boolean isTriggeredByDrawerItem) {
        if (!isTriggeredByDrawerItem) {
            insertItemFragment(bundle);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemFragment != null && itemFragment.isVisible()) {
            itemFragment.onBackButtonPressed();
        } else if (editMemoFragment != null && editMemoFragment.isVisible()) {
            editMemoFragment.onBackButtonPressed();
        } else if (archiveFragment != null && archiveFragment.isVisible()) {
            archiveFragment.onBackButtonPressed();
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
    public void syncDrawerToggleState() {
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_notes:
                if (editMemoFragment != null && editMemoFragment.isVisible()) {
                    editMemoFragment.saveMemo(true);
                }
                insertItemFragment(null);
                break;
            case R.id.drawer_item_archive:
                if (editMemoFragment != null && editMemoFragment.isVisible()) {
                    editMemoFragment.saveMemo(true);
                }
                insertArchiveFragment();
                break;
            case R.id.drawer_item_add:
                if (editMemoFragment != null && editMemoFragment.isVisible()) {
                    editMemoFragment.saveMemo(true);
                }
                onAddButtonPressed();
                break;
            default:
                return false;
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isEditFragmentVisible = editMemoFragment != null && editMemoFragment.isVisible();
        boolean isArchiveFragmentVisible = archiveFragment != null && archiveFragment.isVisible();
        FragmentManager manager = getSupportFragmentManager();
        if (isEditFragmentVisible) {
            manager.putFragment(outState, EDIT_FRAGMENT_NAME, editMemoFragment);
        } else if (isArchiveFragmentVisible) {
            manager.putFragment(outState, ARCHIVE_FRAGMENT_NAME, archiveFragment);
        } else {
            manager.putFragment(outState, ITEM_FRAGMENT_NAME, itemFragment);
        }
        outState.putBoolean(EDIT_FRAG_VISIBILITY_KEY, isEditFragmentVisible);
        outState.putBoolean(ARCHIVE_FRAG_VISIBILITY_KEY, isArchiveFragmentVisible);
    }

    public static int getCOLUMNS() {
        return COLUMNS;
    }
}
