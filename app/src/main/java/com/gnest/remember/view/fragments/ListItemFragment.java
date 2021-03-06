package com.gnest.remember.view.fragments;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gnest.remember.App;
import com.gnest.remember.R;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.presenter.IListFragmentPresenter;
import com.gnest.remember.presenter.ListFragmentPresenter;
import com.gnest.remember.services.AlarmReceiver;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.activity.MainActivity;
import com.gnest.remember.view.helper.ItemTouchHelperCallback;
import com.gnest.remember.services.AlarmService;
import com.gnest.remember.view.menu.ActionMenu;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;
import java.util.Locale;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import florent37.github.com.rxlifecycle.RxLifecycle;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

public class ListItemFragment extends MvpFragment<IListFragmentView, IListFragmentPresenter>
        implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, IListFragmentView {

    public static final String BUNDLE_KEY = "Bundle key";
    public static final String EXPANDED_KEY = "Expanded key";
    public static final String ARG_COLUMN_COUNT = "ColumnCount";
    public static final String ARG_MEMO_SIZE = "MemoSize";
    public static final String ARG_MEMO_MARGINS = "MemoMargins";
    private static final String SAVED_LAYOUT_MANAGER = "Saved layout manager";
    private static final String SAVED_STATE_KEY = "Saved state key";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.items_fragment)
    LinearLayout layout;
    @BindView(R.id.memo_list)
    RecyclerView recyclerView;
    @BindView(R.id.ItemFragmentToolbar)
    Toolbar toolbar;

    @BindString(R.string.note_archived_message)
    String noteArchivedMessage;
    @BindString(R.string.note_archived_message_1)
    String noteArchivedMessage1;
    @BindString(R.string.note_archived_message_2)
    String noteArchivedMessage2;
    @BindString(R.string.note_removed_message)
    String noteRemovedMessage;
    @BindString(R.string.note_removed_message_1)
    String noteRemovedMessage1;
    @BindString(R.string.note_removed_message_2)
    String noteRemovedMessage2;
    @BindString(R.string.send_memo_intent_title)
    String sendMemoIntentTitle;

    private View mView;
    private Unbinder mUnbinder;
    private int mColumnCount;
    private int mMemoSize;
    private int mMargins;
    private OnListItemFragmentInteractionListener mListener;
    private MySelectableAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ActionMode mActionMode;
    private ActionMenu mActionMenu;
    private MyGridLayoutManager mMyGridLayoutManager;
    private DrawerLayout mDrawerLayout;
    private Bundle mSavedState;


    public static ListItemFragment newInstance(int columnCount, int memoSize, int margins) {
        ListItemFragment fragment = new ListItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_MEMO_SIZE, memoSize);
        args.putInt(ARG_MEMO_MARGINS, margins);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActionMenu = new ActionMenu(this);
        RefWatcher refWatcher = App.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mUnbinder = ButterKnife.bind(this, mView);
        if (getActivity() != null) {
            mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
        }
        WeakReference<LinearLayout> layoutWeakReference = new WeakReference<>(layout);
        Glide.with(this).load(R.drawable.itemfragment_background_pin_board).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                layoutWeakReference.get().setBackground(resource);
            }
        });
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mMemoSize = getArguments().getInt(ARG_MEMO_SIZE);
            mMargins = getArguments().getInt(ARG_MEMO_MARGINS);
        }
        Context context = mView.getContext();
        mMyGridLayoutManager = new MyGridLayoutManager(context, mColumnCount);
        recyclerView.setLayoutManager(mMyGridLayoutManager);
        mAdapter = new MySelectableAdapter(mMemoSize, mMargins);
        mAdapter.setActionListener(this);
        mMyGridLayoutManager.setExpandListener(mAdapter);

        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        presenter.loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                mListener.syncDrawerToggleState();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ab_itemfragment, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListItemFragmentInteractionListener) {
            mListener = (OnListItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListItemFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mAdapter != null) {
            mAdapter.setActionListener(null);
        }
        if (mMyGridLayoutManager != null) {
            mMyGridLayoutManager.setExpandListener(null);
        }
        compositeDisposable.dispose();
        shutDownActionMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    @Override
    @NonNull
    public IListFragmentPresenter createPresenter() {
        return new ListFragmentPresenter();
    }


    @Override
    public void setData(RealmResults<Memo> data) {
        mAdapter.setMemos(data);
        mAdapter.notifyDataSetChanged();
        checkStateRestore();
    }

    private void checkStateRestore() {
        Bundle bundle = null;
        //Restoring state after config change
        if (mSavedState != null) {
            bundle = mSavedState;
        }
        //Restoring state after returning from edit mode
        else if (getArguments() != null) {
            bundle = getArguments().getBundle(BUNDLE_KEY);
        }
        if (bundle != null) {
            restoreLayoutManagerState(bundle.getInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY),
                    bundle.getBoolean(EXPANDED_KEY),
                    bundle.getParcelable(SAVED_LAYOUT_MANAGER),
                    bundle.getInt(MyGridLayoutManager.POSITION_KEY));
        }
    }

    private void restoreLayoutManagerState(int orientation, boolean isItemsExtended, @Nullable Parcelable state, int anchorPosition) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            mMyGridLayoutManager.setSpanCount(1);
        }
        mMyGridLayoutManager.setOrientation(orientation);
        mAdapter.setItemsExpanded(isItemsExtended);
        if (state != null) {
            mMyGridLayoutManager.onRestoreInstanceState(state);
        }
        mMyGridLayoutManager.scrollToPosition(anchorPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle state = saveListState();
        outState.putBundle(SAVED_STATE_KEY, state);
    }

    @NonNull
    public Bundle saveListState() {
        mSavedState = new Bundle();
        mSavedState.putParcelable(SAVED_LAYOUT_MANAGER, mMyGridLayoutManager.onSaveInstanceState());
        mSavedState.putBoolean(EXPANDED_KEY, mAdapter.isItemsExpanded());
        mSavedState.putInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY, mMyGridLayoutManager.getOrientation());
        mSavedState.putInt(MyGridLayoutManager.POSITION_KEY, mMyGridLayoutManager.getLastPosition());
        return mSavedState;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSavedState = savedInstanceState.getParcelable(SAVED_STATE_KEY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                mListener.onAddButtonPressed();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onArchiveButtonPressed() {
        presenter.processArchiveActionOnSelected(mAdapter.getSelectedIds());
    }

    @Override
    public void onShareButtonPressed() {
        presenter.processShare(mAdapter.getSelectedIds());
    }

    @Override
    public void onDeleteButtonPressed() {
        presenter.processDeleteSelectedMemos(mAdapter.getSelectedIds());
    }

    @Override
    public void onPerformSwipeDismiss(int memoId, int memoPosition) {
        presenter.processSwipeDismiss(memoId, memoPosition);
    }

    @Override
    public Snackbar getArchiveSnackbar(int numOfNotes) {
        return createAndShowSnackbar(getArchiveActionPluralForm(getPlural(numOfNotes)), numOfNotes);
    }

    @Override
    public Snackbar getDeleteSnackbar(int numOfNotes) {
        return createAndShowSnackbar(getRemoveActionPluralForm(getPlural(numOfNotes)), numOfNotes);
    }

    @NonNull
    private Snackbar createAndShowSnackbar(String pluralForm, int numOfNotes) {
        String text = numOfNotes + " " + pluralForm;
        View view = getView();
        if (view == null) {
            throw new NullPointerException("view == null");
        }
          /*Have to pass a dummy listener, so action button could be visible
        * Undo action is being processed by rxSnackbar in presenter*/
        Snackbar snackbar = Snackbar
                .make(getView(), text, Snackbar.LENGTH_SHORT)
                .setAction(android.R.string.cancel, v -> {
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
        return snackbar;
    }

    String getArchiveActionPluralForm(int plural) {
        switch (plural) {
            case 2:
                return noteArchivedMessage2;
            case 1:
                return noteArchivedMessage1;
            default:
                return noteArchivedMessage;
        }
    }

    private String getRemoveActionPluralForm(int plural) {
        switch (plural) {
            case 2:
                return noteRemovedMessage2;
            case 1:
                return noteRemovedMessage1;
            default:
                return noteRemovedMessage;
        }
    }

    private int getPlural(int numOfNotes) {
        switch (Locale.getDefault().getLanguage()) {
            case "en":
                return numOfNotes == 1 ? 0 : 1;
            case "ru":
                return (numOfNotes % 10 == 1 && numOfNotes % 100 != 11 ?
                        0 : numOfNotes % 10 >= 2 && numOfNotes % 10 <= 4 &&
                        (numOfNotes % 100 < 10 || numOfNotes % 100 >= 20) ? 1 : 2);
            default:
                return 0;
        }
    }

    @Override
    public void shareMemoText(String memoText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, memoText);
        Intent chooserIntent = Intent.createChooser(intent, sendMemoIntentTitle);
        mActionMode.finish();
        startActivity(chooserIntent);
    }

    @Override
    public void setAlarm(int memoId, long alarmDate, String notificationText, boolean isAlarmSet, boolean isAlarmMovedToMainScreen) {
        Intent intent = AlarmReceiver.getReceiverIntent(getContext(), memoId, notificationText, alarmDate, isAlarmSet, isAlarmMovedToMainScreen);
        mListener.sendBroadcast(intent);
    }

    @Override
    public void removeAlarm(int memoId) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            Intent intent = AlarmService.getServiceIntent(activity, null, memoId, true);
            PendingIntent pendingIntent = PendingIntent.getService(activity, memoId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
        }
    }

    @Override
    public void removeFromCalendar(int id) {
        mListener.removeFromCalendar(id);
    }

    @Override
    public boolean isNotificationVisible(int id) {
        if (Build.VERSION.SDK_INT >= 23) {
            FragmentActivity activity = getActivity();
            NotificationManager manager = null;
            if (activity != null) {
                manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (manager != null) {
                for (StatusBarNotification notification : manager.getActiveNotifications()) {
                    if (notification.getId() == id) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            Intent notificationIntent = new Intent(getContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
            return pendingIntent != null;
        }
    }

    @Override
    public void openFromNotification(int id) {
        Disposable disposable = RxLifecycle.with(getLifecycle())
                .onResume()
                .subscribe(event -> presenter.processOpenFromNotification(id));
        compositeDisposable.add(disposable);
    }

    @Override
    public void closeNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) App.self().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(id);
        }
    }

    @Override
    public void swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        presenter.processMemoSwap(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public ActionMode getActionMode() {
        return mActionMode;
    }

    @Override
    public void showActionMode() {
        if (mActionMenu != null && getActivity() != null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionMenu);
        }
    }

    @Override
    public void shutDownActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void updateContextActionMenu(int numOfSelectedItems) {
        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(numOfSelectedItems != 0 ? numOfSelectedItems : ""));
        }
    }

    @Override
    public void switchMultiSelect(boolean switchedOn) {
        mAdapter.switchMultiSelect(switchedOn);
    }

    @Override
    public void clearSelection() {
        mAdapter.clearSelectedList();
    }

    @Override
    public void setShareButtonVisibility(boolean isVisible) {
        if (mActionMenu != null) {
            mActionMenu.setShareButtonVisibility(isVisible);
        }
    }

    @Override
    public void onSingleChoiceMemoClicked(Memo memo) {
        presenter.processSingleChoiceClick(memo, LinearLayoutManager.VERTICAL);
    }

    public void onBackButtonPressed() {
        presenter.processPressBackButton(LinearLayoutManager.VERTICAL, LinearLayoutManager.HORIZONTAL, MainActivity.getColumns());
    }

    @Override
    public MySelectableAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public MyGridLayoutManager getLayoutManager() {
        return mMyGridLayoutManager;
    }

    @Override
    public OnListItemFragmentInteractionListener getInteractionListener() {
        return mListener;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListItemFragmentInteractionListener {
        void onAddButtonPressed();

        void onBackButtonPressed();

        void onEnterEditMode(int memoId);

        void syncDrawerToggleState();

        void sendBroadcast(Intent intent);

        void removeFromCalendar(int id);
    }
}
