package com.gnest.remember.view.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gnest.remember.R;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.presenter.IListFragmentPresenter;
import com.gnest.remember.presenter.ListFragmentPresenter;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.helper.ItemTouchHelperCallback;
import com.gnest.remember.model.services.AlarmService;
import com.gnest.remember.view.menu.ActionMenu;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;


public class ListItemFragment extends MvpFragment<IListFragmentView, IListFragmentPresenter>
        implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, IListFragmentView {

    public static final String BUNDLE_KEY = "Bundle key";
    public static final String EXPANDED_KEY = "Expanded key";
    public static final String ARG_COLUMN_COUNT = "ColumnCount";
    public static final String ARG_MEMO_SIZE = "MemoSize";
    public static final String ARG_MEMO_MARGINS = "MemoMargins";
    private final BehaviorSubject<Boolean> dataLoadedSubject = BehaviorSubject.create();

    private int mColumnCount;
    private int mMemoSize;
    private int mMargins;
    private OnListItemFragmentInteractionListener mListener;
    private MySelectableAdapter mAdapter;
    private View mView;
    private ItemTouchHelper itemTouchHelper;
    private ActionMode actionMode;
    private ActionMenu actionMenu;
    private MyGridLayoutManager mMyGridLayoutManager;

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
        actionMenu = new ActionMenu(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_item_list, container, false);
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
        RecyclerView recyclerView = mView.findViewById(R.id.memo_list);
        Context context = mView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mMyGridLayoutManager = new MyGridLayoutManager(context, mColumnCount, mMemoSize, mMargins);
            recyclerView.setLayoutManager(mMyGridLayoutManager);
        }
        mAdapter = new MySelectableAdapter(mMemoSize, mMargins);
        mAdapter.setActionListener(this);
        mMyGridLayoutManager.setExpandListener(mAdapter);

        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        presenter.loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = mView.findViewById(R.id.ItemFragmentToolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mListener.syncDrawerToggleState();
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        shutDownActionMode();
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
        returnFromEditMode();
        dataLoadedSubject.onNext(true);
    }

    private void returnFromEditMode() {
        if (getArguments() != null) {
            Bundle bundle = getArguments().getBundle(BUNDLE_KEY);
            if (bundle != null) {
                int lastPosition = bundle.getInt(MyGridLayoutManager.POSITION_KEY);
                int lastOrientation = bundle.getInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY);
                boolean isExpanded = bundle.getBoolean(EXPANDED_KEY);
                presenter.processReturnFromEditMode(lastPosition, lastOrientation, isExpanded);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = getArguments().getBundle(BUNDLE_KEY);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(MyGridLayoutManager.LM_SCROLL_ORIENTATION_KEY, mMyGridLayoutManager.getOrientation());
        bundle.putInt(MyGridLayoutManager.POSITION_KEY, mMyGridLayoutManager.getLastPosition());
        bundle.putBoolean(EXPANDED_KEY, mAdapter.isItemsExpanded());
        getArguments().putBundle(BUNDLE_KEY, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                mListener.onAddButtonPressed();
                return true;
            case android.R.id.home:
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public Observable<Boolean> showConfirmArchiveActionPopup(PublishSubject<Boolean> subject, int numOfNotes) {
        View layout = getLayoutInflater().inflate(R.layout.layout_popup_confirmation_dismiss, getActivity().findViewById(R.id.container_popup_cancel_dismiss));
        PopupWindow popupWindow = setUpPopupWindow(layout, subject, numOfNotes);
        setUpCancelArchiveActionMessage(layout, numOfNotes);
        return getPopUpObservable(subject, popupWindow);
    }

    @Override
    public Observable<Boolean> showConfirmRemovePopup(PublishSubject<Boolean> subject, int numOfNotes) {
        View layout = getLayoutInflater().inflate(R.layout.layout_popup_confirmation_dismiss, getActivity().findViewById(R.id.container_popup_cancel_dismiss));
        PopupWindow popupWindow = setUpPopupWindow(layout, subject, numOfNotes);
        setUpCancelRemoveMessage(layout, numOfNotes);
        return getPopUpObservable(subject, popupWindow);
    }

    @NonNull
    private PopupWindow setUpPopupWindow(View layout, PublishSubject<Boolean> subject, int numOfNotes) {
        TextView cancel = layout.findViewById(R.id.btn_cancel_dismiss);

        PopupWindow popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, false);

        cancel.setOnClickListener(v -> {
            for (int x = 0; x < numOfNotes; x++) {
                subject.onNext(true);
            }
            subject.onCompleted();
            popupWindow.dismiss();
        });
        popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, getYOffset());

        return popupWindow;
    }

    private int getYOffset() {
        //Checking if device has on screen buttons and calculating offset;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    void setUpCancelArchiveActionMessage(View layout, int numOfNotes) {
        TextView cancelMessage = layout.findViewById(R.id.cancelText);
        int plural = getPlural(numOfNotes);
        String text = numOfNotes + " " + getArchiveActionPluralForm(plural);
        cancelMessage.setText(text);
    }

    void setUpCancelRemoveMessage(View layout, int numOfNotes) {
        TextView cancelMessage = layout.findViewById(R.id.cancelText);
        int plural = getPlural(numOfNotes);
        String text = numOfNotes + " " + getRemovePluralForm(plural);
        cancelMessage.setText(text);
    }

    String getArchiveActionPluralForm(int plural) {
        switch (plural) {
            case 2:
                return getString(R.string.note_archived_message_2);
            case 1:
                return getString(R.string.note_archived_message_1);
            default:
                return getString(R.string.note_archived_message);
        }
    }

    private String getRemovePluralForm(int plural) {
        switch (plural) {
            case 2:
                return getString(R.string.note_removed_message_2);
            case 1:
                return getString(R.string.note_removed_message_1);
            default:
                return getString(R.string.note_removed_message);
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

    @NonNull
    private Observable<Boolean> getPopUpObservable(PublishSubject<Boolean> subject, PopupWindow popupWindow) {
        return subject
                .take(2000, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(popupWindow::dismiss);
    }

    @Override
    public void removeAlarm(int memoId) {
        FragmentActivity activity = getActivity();
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = AlarmService.getServiceIntent(activity, null, memoId);
        PendingIntent pendingIntent = PendingIntent.getService(activity, memoId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null) {
            manager.cancel(pendingIntent);
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
    public void shareMemoText(String memoText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, memoText);
        Intent chooserIntent = Intent.createChooser(intent, getString(R.string.send_memo_intent_title));
        actionMode.finish();
        startActivity(chooserIntent);
    }

    @Override
    public void swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        presenter.processMemoSwap(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public ActionMode getActionMode() {
        return actionMode;
    }

    @Override
    public void showActionMode() {
        if (actionMenu != null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionMenu);
        }
    }

    @Override
    public void shutDownActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void updateContextActionMenu(int numOfSelectedItems) {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(numOfSelectedItems != 0 ? numOfSelectedItems : ""));
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
        if (actionMenu != null) {
            actionMenu.setShareButtonVisibility(isVisible);
        }
    }

    @Override
    public void onSingleChoiceMemoClicked(Memo memo) {
        presenter.processSingleChoiceClick(memo, LinearLayoutManager.VERTICAL);
    }

    public void shutdownMemoAlarm(int position) {
        presenter.processMemoAlarmShutdown(mAdapter.getMemos().get(position));
        mAdapter.notifyItemChanged(position);
    }

    public void onBackButtonPressed() {
        presenter.processPressBackButton(LinearLayoutManager.VERTICAL, LinearLayoutManager.HORIZONTAL);
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

    public BehaviorSubject<Boolean> getDataLodingSubject() {
        return dataLoadedSubject;
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
    }
}
