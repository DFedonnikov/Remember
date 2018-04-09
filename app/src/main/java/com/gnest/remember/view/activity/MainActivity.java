package com.gnest.remember.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.gnest.remember.App;
import com.gnest.remember.R;
import com.gnest.remember.view.fragments.ArchiveItemFragment;
import com.gnest.remember.view.fragments.EditMemoFragment;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.services.AlarmService;
import com.gnest.remember.view.fragments.SettingsFragment;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements
        EditMemoFragment.OnEditMemoFragmentInteractionListener,
        ListItemFragment.OnListItemFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private static final String EDIT_FRAGMENT_NAME = "Edit";
    private static final String ARCHIVE_FRAGMENT_NAME = "Archive";
    private static final String ITEM_FRAGMENT_NAME = "Notes";
    private static final String SETTINGS_FRAGMENT_NAME = "Settings";
    private static final String BACK_STACKED_FRAGMENT_NAME = "BackStacked";
    private static final String EDIT_FRAG_VISIBILITY_KEY = "Edit frag visibility key";
    private static final String ARCHIVE_FRAG_VISIBILITY_KEY = "Archive frag visibility key";
    private static final String SETTINGS_FRAGMENT_VISIBILITY_KEY = "Settings frag visibility key";
    public static final int ITEM_MARGINS_DP = 12;
    public static final int MEMO_SIZE_DP = 160;

    private ListItemFragment mItemFragment;
    private EditMemoFragment mEditMemoFragment;
    private ArchiveItemFragment mArchiveFragment;
    private SettingsFragment mSettingsFragment;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Unbinder mUnbinder;
    private String backStackedFragmentTitle;

    private RxPermissions rxPermissions;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static int sColumns;
    private static int sMemoSizePx;
    private static int sMarginsPx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RefWatcher refWatcher = App.getRefWatcher();
        refWatcher.watch(this);
        mUnbinder = ButterKnife.bind(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        rxPermissions = new RxPermissions(this);
        calculateColumnsAndMemoSize();
        configureDrawer();
        if (savedInstanceState != null) {
            boolean isEditFragVisible = savedInstanceState.getBoolean(EDIT_FRAG_VISIBILITY_KEY);
            boolean isArchiveFragVisible = savedInstanceState.getBoolean(ARCHIVE_FRAG_VISIBILITY_KEY);
            boolean isSettingsFragVisible = savedInstanceState.getBoolean(SETTINGS_FRAGMENT_VISIBILITY_KEY);
            FragmentManager manager = getSupportFragmentManager();
            if (isEditFragVisible) {
                mEditMemoFragment = (EditMemoFragment) manager.getFragment(savedInstanceState, EDIT_FRAGMENT_NAME);
            } else if (isArchiveFragVisible) {
                mArchiveFragment = (ArchiveItemFragment) manager.getFragment(savedInstanceState, ARCHIVE_FRAGMENT_NAME);
                setDimenArgs(mArchiveFragment);
                setTitle(R.string.archive);
            } else if (isSettingsFragVisible) {
                mSettingsFragment = (SettingsFragment) manager.getFragment(savedInstanceState, SETTINGS_FRAGMENT_NAME);
                restoreBackStackedFragment(manager, savedInstanceState);
                setTitle(R.string.settings);
            } else {
                mItemFragment = (ListItemFragment) manager.getFragment(savedInstanceState, ITEM_FRAGMENT_NAME);
                setDimenArgs(mItemFragment);
                setTitle(getString(R.string.notes));
            }
        } else {
            insertItemFragment(null);
        }
    }

    private void restoreBackStackedFragment(FragmentManager manager, Bundle savedInstanceState) {
        String backStackedFragmentName = savedInstanceState.getString(BACK_STACKED_FRAGMENT_NAME, "");
        Fragment backStackedFragment = manager.getFragment(savedInstanceState, backStackedFragmentName);
        switch (backStackedFragmentName) {
            case ITEM_FRAGMENT_NAME:
                mItemFragment = (ListItemFragment) backStackedFragment;
                setDimenArgs(mItemFragment);
                break;
            case ARCHIVE_FRAGMENT_NAME:
                mArchiveFragment = (ArchiveItemFragment) backStackedFragment;
                setDimenArgs(mArchiveFragment);
                break;
            case EDIT_FRAGMENT_NAME:
                mEditMemoFragment = (EditMemoFragment) backStackedFragment;
        }
    }

    private <T extends Fragment> void setDimenArgs(T fragment) {
        Bundle args = fragment.getArguments();
        if (args != null) {
            args.putInt(ListItemFragment.ARG_COLUMN_COUNT, sColumns);
            args.putInt(ListItemFragment.ARG_MEMO_SIZE, sMemoSizePx);
            args.putInt(ListItemFragment.ARG_MEMO_MARGINS, sMarginsPx);
        }
    }

    private void calculateColumnsAndMemoSize() {
        int screenWidthDP = getResources().getConfiguration().screenWidthDp;
        sColumns = screenWidthDP / MEMO_SIZE_DP;
        float density = getResources().getDisplayMetrics().density;
        sMemoSizePx = (int) (((screenWidthDP - 2 * ITEM_MARGINS_DP * sColumns) / sColumns) * density + 0.5);
        sMarginsPx = (int) (ITEM_MARGINS_DP * density + 0.5);
    }

    private void configureDrawer() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, findViewById(R.id.toolbar), R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.addDrawerListener(mActionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRunFromNotification(); //To be executed if activity launched from notification
    }

    private void checkRunFromNotification() {
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra(AlarmService.NOTIFICATION_MEMO_ID, -1);
            boolean isOnMainScreen = intent.getBooleanExtra(AlarmService.IS_ON_MAIN_SCREEN, true);
            intent.removeExtra(AlarmService.NOTIFICATION_MEMO_ID);
            intent.removeExtra(AlarmService.IS_ON_MAIN_SCREEN);
            if (id != -1) {
                if (mItemFragment != null && isOnMainScreen) {
                    mItemFragment.openFromNotification(id);
                } else {
                    insertArchiveFragment();
                    if (mArchiveFragment != null) {
                        mArchiveFragment.openFromNotification(id);
                    }
                }
                removeFromCalendar(id);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        compositeDisposable.dispose();
        rxPermissions = null;
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

    private void insertSettingsFragment() {
        insertFragment(SettingsFragment.class, null);
        setTitle(R.string.settings);
    }

    private <T extends Class<? extends Fragment>> void insertFragment(T fragmentClass, Bundle bundle) {
        Fragment fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentClass.equals(ArchiveItemFragment.class)) {
            fragment = mArchiveFragment = ArchiveItemFragment.newInstance(sColumns, sMemoSizePx, sMarginsPx);
            mItemFragment = null;
            mEditMemoFragment = null;
            mSettingsFragment = null;
        } else if (fragmentClass.equals(EditMemoFragment.class)) {
            fragment = mEditMemoFragment = EditMemoFragment.newInstance(bundle);
            mItemFragment = mArchiveFragment = null;
            mSettingsFragment = null;
        } else if (fragmentClass.equals(SettingsFragment.class)) {
            fragment = mSettingsFragment = SettingsFragment.newInstance();
            ft.addToBackStack(null);
            checkSaveListLayoutState();
            backStackedFragmentTitle = getTitle().toString();
        } else {
            fragment = mItemFragment = ListItemFragment.newInstance(sColumns, sMemoSizePx, sMarginsPx);
            if (bundle != null && mItemFragment.getArguments() != null) {
                mItemFragment.getArguments().putBundle(ListItemFragment.BUNDLE_KEY, bundle);
            }
            mArchiveFragment = null;
            mEditMemoFragment = null;
            mSettingsFragment = null;
        }

        ft.replace(R.id.current_fragment, fragment, null);
        ft.commit();
    }

    private void checkSaveListLayoutState() {
        if (mItemFragment != null) {
            mItemFragment.saveListState();
        } else if (mArchiveFragment != null) {
            mArchiveFragment.saveListState();
        }
    }

    @Override
    public void onReturnFromEditFragmentInteraction(Bundle bundle) {
        insertItemFragment(bundle);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (mItemFragment != null && mItemFragment.isVisible()) {
            mItemFragment.onBackButtonPressed();
        } else if (mEditMemoFragment != null && mEditMemoFragment.isVisible()) {
            mEditMemoFragment.onBackButtonPressed();
        } else if (mArchiveFragment != null && mArchiveFragment.isVisible()) {
            mArchiveFragment.onBackButtonPressed();
        } else if (mSettingsFragment != null && mSettingsFragment.isVisible()) {
            getSupportFragmentManager().popBackStackImmediate();
            if (backStackedFragmentTitle != null) {
                setTitle(backStackedFragmentTitle);
            } else {
                setTitle(R.string.app_name);
            }
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
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (mSettingsFragment != null && mSettingsFragment.isVisible()) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        checkSaveMemo();
        switch (item.getItemId()) {
            case R.id.drawer_item_notes:
                insertItemFragment(null);
                break;
            case R.id.drawer_item_archive:
                insertArchiveFragment();
                break;
            case R.id.drawer_item_add:
                onAddButtonPressed();
                break;
            case R.id.drawer_settings:
                insertSettingsFragment();
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

    private void checkSaveMemo() {
        if (mEditMemoFragment != null && mEditMemoFragment.isVisible()) {
            mEditMemoFragment.saveMemo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isEditFragmentVisible = mEditMemoFragment != null && mEditMemoFragment.isVisible();
        boolean isArchiveFragmentVisible = mArchiveFragment != null && mArchiveFragment.isVisible();
        boolean isSettingsFragmentVisible = mSettingsFragment != null && mSettingsFragment.isVisible();
        FragmentManager manager = getSupportFragmentManager();
        if (isEditFragmentVisible) {
            manager.putFragment(outState, EDIT_FRAGMENT_NAME, mEditMemoFragment);
        } else if (isArchiveFragmentVisible) {
            manager.putFragment(outState, ARCHIVE_FRAGMENT_NAME, mArchiveFragment);
        } else if (isSettingsFragmentVisible) {
            manager.putFragment(outState, SETTINGS_FRAGMENT_NAME, mSettingsFragment);
            saveBackStackedFragmentToManager(manager, outState);
        } else {
            manager.putFragment(outState, ITEM_FRAGMENT_NAME, mItemFragment);
        }
        outState.putBoolean(EDIT_FRAG_VISIBILITY_KEY, isEditFragmentVisible);
        outState.putBoolean(ARCHIVE_FRAG_VISIBILITY_KEY, isArchiveFragmentVisible);
        outState.putBoolean(SETTINGS_FRAGMENT_VISIBILITY_KEY, isSettingsFragmentVisible);
    }

    private void saveBackStackedFragmentToManager(FragmentManager manager, Bundle outState) {
        Fragment fragmentToSave = null;
        String savedFragmentName = "";
        if (mItemFragment != null) {
            fragmentToSave = mItemFragment;
            savedFragmentName = ITEM_FRAGMENT_NAME;
        } else if (mArchiveFragment != null) {
            fragmentToSave = mArchiveFragment;
            savedFragmentName = ARCHIVE_FRAGMENT_NAME;
        } else if (mEditMemoFragment != null) {
            fragmentToSave = mEditMemoFragment;
            savedFragmentName = EDIT_FRAGMENT_NAME;
        }
        if (fragmentToSave != null) {
            outState.putString(BACK_STACKED_FRAGMENT_NAME, savedFragmentName);
            manager.putFragment(outState, savedFragmentName, fragmentToSave);
        }
    }

    @Override
    public void addToCalendar(int memoId, String description, long timeInMillis) {
        CalendarUpdateStrategy strategy = getEventId(memoId) == -1 ? CalendarUpdateStrategy.ADD : CalendarUpdateStrategy.UPDATE;
        processCalendar(memoId, description, timeInMillis, strategy);
    }

    @Override
    public void removeFromCalendar(int memoId) {
        processCalendar(memoId, null, -1, CalendarUpdateStrategy.DELETE);
    }

    private void processCalendar(int memoId, String description, long timeInMillis, CalendarUpdateStrategy strategy) {
        ContentResolver contentResolver = getContentResolver();
        Disposable disposable = rxPermissions.requestEachCombined(Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR)
                .subscribe(permission -> {
                    if (permission.granted) {
                        switch (strategy) {
                            case ADD:
                                addToCalendarInternal(memoId, description, timeInMillis, contentResolver);
                                break;
                            case UPDATE:
                                updateCalendarInternal(memoId, description, timeInMillis, contentResolver);
                                break;
                            case DELETE:
                                removeFromCalendarInternal(memoId, contentResolver);
                                break;
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Toast.makeText(this, R.string.calendar_perm_denied_toast,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.calendar_perm_denied_without_ask_again_toast,
                                Toast.LENGTH_LONG).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void addToCalendarInternal(int memoId, String description, long timeInMillis, ContentResolver contentResolver) {
        long calendarId = getCalendarId(contentResolver);
        ContentValues contentValues = getCalendarEventContentValues(description, timeInMillis, calendarId);
        @SuppressLint("MissingPermission") Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
        if (uri != null) {
            long eventId = Long.parseLong(uri.getLastPathSegment());
            processCalendarEventIdInSharedPref(memoId, eventId, CalendarUpdateStrategy.ADD);
        }
        Toast.makeText(this, R.string.calendar_event_added_toast,
                Toast.LENGTH_SHORT).show();
    }

    private void updateCalendarInternal(int memoId, String description, long timeInMillis, ContentResolver contentResolver) {
        long calendarId = getCalendarId(contentResolver);
        long eventId = getEventId(memoId);
        ContentValues contentValues = getCalendarEventContentValues(description, timeInMillis, calendarId);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        contentResolver.update(updateUri, contentValues, null, null);
        Toast.makeText(this, R.string.calendar_event_updated_toast,
                Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private ContentValues getCalendarEventContentValues(String description, long timeInMillis, long calendarId) {
        ContentValues contentValues = new ContentValues();
        long endDate = timeInMillis + 1000 * 60 * 60;
        contentValues.put(CalendarContract.Events.DTSTART, timeInMillis);
        contentValues.put(CalendarContract.Events.DTEND, endDate);
        contentValues.put(CalendarContract.Events.TITLE, getResources().getString(R.string.notification_title));
        contentValues.put(CalendarContract.Events.DESCRIPTION, description);
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        return contentValues;
    }

    @SuppressLint("MissingPermission")
    private long getCalendarId(ContentResolver contentResolver) {
        long calendarId = 0;
        String selection = CalendarContract.Calendars.VISIBLE + " = 1 AND "
                + CalendarContract.Calendars.IS_PRIMARY + " = 1";
        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cur = contentResolver.query(calendarUri, null, selection, null, null);
        if (cur != null && cur.getCount() <= 0) {
            cur = contentResolver.query(calendarUri, null, null, null, null);
        }
        if (cur != null && cur.moveToFirst()) {
            calendarId = cur.getLong(cur.getColumnIndex(CalendarContract.Calendars._ID));
        }

        if (cur != null) {
            cur.close();
        }
        return calendarId;
    }

    private void processCalendarEventIdInSharedPref(int memoId, long eventId, CalendarUpdateStrategy strategy) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String key = getCalendarEventKey(memoId);
        switch (strategy) {
            case ADD:
                editor.putLong(key, eventId);
                break;
            case DELETE:
                editor.remove(key);
                break;
        }
        editor.apply();
    }

    @NonNull
    private String getCalendarEventKey(int memoId) {
        return getResources().getString(R.string.calendar_event_base_key) + memoId;
    }

    private void removeFromCalendarInternal(int memoId, ContentResolver contentResolver) {
        long eventId = getEventId(memoId);
        if (eventId != -1) {
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            contentResolver.delete(deleteUri, null, null);
            processCalendarEventIdInSharedPref(memoId, eventId, CalendarUpdateStrategy.DELETE);
        }
    }

    private long getEventId(int memoId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getCalendarEventKey(memoId);
        return preferences.getLong(key, -1);
    }


    public static int getColumns() {
        return sColumns;
    }

    private enum CalendarUpdateStrategy {
        ADD,
        DELETE,
        UPDATE
    }

}
