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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gnest.remember.R;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.presenter.IListFragmentPresenter;
import com.gnest.remember.presenter.ListFragmentPresenter;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.activity.MainActivity;
import com.gnest.remember.view.helper.ItemTouchHelperCallback;
import com.gnest.remember.model.services.AlarmService;
import com.gnest.remember.view.menu.ActionMenu;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.concurrent.TimeUnit;

import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;


public class ListItemFragment extends MvpFragment<IListFragmentView, IListFragmentPresenter>
        implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, IListFragmentView {

    private final BehaviorSubject<Boolean> dataLoadedSubject = BehaviorSubject.create();

    private int mColumnCount;
    private int mMemoSize;
    private int mMargins;
    private OnListItemFragmentInteractionListener mListener;
    private MySelectableAdapter mAdapter;
    private View mView;
    private ItemTouchHelper itemTouchHelper;
    private android.support.v7.view.ActionMode actionMode;
    private ActionMenu actionMenu;
    private MyGridLayoutManager mMyGridLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListItemFragment() {
    }

    public static ListItemFragment newInstance(int columnCount, int memoSize, int margins) {
        ListItemFragment fragment = new ListItemFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_COLUMN_COUNT, columnCount);
        args.putInt(MainActivity.ARG_MEMO_SIZE, memoSize);
        args.putInt(MainActivity.ARG_MEMO_MARGINS, margins);
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
            mColumnCount = getArguments().getInt(MainActivity.ARG_COLUMN_COUNT);
            mMemoSize = getArguments().getInt(MainActivity.ARG_MEMO_SIZE);
            mMargins = getArguments().getInt(MainActivity.ARG_MEMO_MARGINS);
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
        mListener.configureDrawer();
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
            Bundle bundle = getArguments().getBundle(MainActivity.BUNDLE_KEY);
            if (bundle != null) {
                int lastPosition = bundle.getInt(MainActivity.POSITION_KEY);
                int lastOrientation = bundle.getInt(MainActivity.LM_SCROLL_ORIENTATION_KEY);
                boolean isExpanded = bundle.getBoolean(MainActivity.EXPANDED_KEY);
                presenter.processReturnFromEditMode(lastPosition, lastOrientation, isExpanded);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = getArguments().getBundle(MainActivity.BUNDLE_KEY);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(MainActivity.LM_SCROLL_ORIENTATION_KEY, mMyGridLayoutManager.getOrientation());
        bundle.putInt(MainActivity.POSITION_KEY, mMyGridLayoutManager.getLastPosition());
        bundle.putBoolean(MainActivity.EXPANDED_KEY, mAdapter.isItemsExpanded());
        getArguments().putBundle(MainActivity.BUNDLE_KEY, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                mListener.onAddButtonPressed();
                return true;
            case android.R.id.home:
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDeleteButtonPressed() {
        presenter.processDeleteSelectedMemos(mAdapter.getSelectedList());
    }

    @Override
    public void onPerformSwipeDismiss(int memoId, int memoPosition, boolean isAlarmSet) {
        presenter.processSwipeDismiss(memoId, memoPosition, isAlarmSet);

    }

    @Override
    public Observable<Boolean> showConfirmPopup(int memoPosition, PublishSubject<Boolean> subject) {
        PopupWindow popupWindow = showPopup(memoPosition, subject);
        return Observable
                .timer(1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(aLong -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    return Observable.just(subject.hasCompleted());
                })
                .doOnUnsubscribe(popupWindow::dismiss);
    }

    private PopupWindow showPopup(int memoPosition, PublishSubject<Boolean> subject) {
        View layout = getLayoutInflater().inflate(R.layout.layout_popup_confirmation_dismiss, getActivity().findViewById(R.id.container_popup_cancel_dismiss));
        setUpCancelMessage(layout);
        TextView cancel = layout.findViewById(R.id.btn_cancel_dismiss);

        PopupWindow popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, false);

        cancel.setOnClickListener(v -> {
            subject.onNext(true);
            subject.onCompleted();
            popupWindow.dismiss();
            getAdapter().notifyItemRangeChanged(memoPosition, getAdapter().getItemCount());
        });
        popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);

        return popupWindow;
    }

    void setUpCancelMessage(View layout) {
        TextView cancelMessage = layout.findViewById(R.id.cancelText);
        cancelMessage.setText(R.string.note_archived_message);
    }

    @Override
    public void removeAlarm(int memoId) {
        FragmentActivity activity = getActivity();
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = AlarmService.getServiceIntent(activity, null, memoId);
        PendingIntent pendingIntent = PendingIntent.getService(activity, memoId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }

    @Override
    public void onShareButtonPressed() {
        presenter.processShare(mAdapter.getSelectedList());
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

        void configureDrawer();
    }
}