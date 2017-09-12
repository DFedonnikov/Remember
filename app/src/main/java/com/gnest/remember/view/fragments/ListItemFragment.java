package com.gnest.remember.view.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.presenter.IListFragmentPresenter;
import com.gnest.remember.presenter.ListFragmentPresenter;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.activity.MainActivity;
import com.gnest.remember.view.helper.ItemTouchHelperCallback;
import com.gnest.remember.model.services.AlarmService;
import com.gnest.remember.view.ActionMenu;
import com.gnest.remember.view.MyGridLayoutManager;
import com.gnest.remember.view.MySelectableAdapter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;


public class ListItemFragment extends MvpFragment<IListFragmentView, IListFragmentPresenter>
        implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, IListFragmentView {

    private static final String ARG_COLUMN_COUNT = "ColumnCount";

    private int mColumnCount;
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

    public static ListItemFragment newInstance(int columnCount) {
        ListItemFragment fragment = new ListItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
        RecyclerView recyclerView = mView.findViewById(R.id.memo_list);
        Context context = mView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mMyGridLayoutManager = new MyGridLayoutManager(context, mColumnCount);
            recyclerView.setLayoutManager(mMyGridLayoutManager);
        }
        mAdapter = new MySelectableAdapter(this);
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
        return new ListFragmentPresenter(getActivity().getSupportLoaderManager());
    }


    @Override
    public void setData(List<ClickableMemo> data) {
        mAdapter.setMemos(data);
        mAdapter.notifyDataSetChanged();
        returnFromEditMode();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDeleteButtonPressed() {
        presenter.processDeleteSelectedMemos(mAdapter.getSelectedList(), mAdapter.getMemos());
        mAdapter.removeSelectedMemos(mAdapter.getSelectedList());
        actionMode.finish();
    }

    @Override
    public void onPerformSwipeDismiss(int memoId, int memoPosition, boolean isAlarmSet) {
        presenter.processDeleteMemo(memoId, memoPosition, mAdapter.getMemos(), isAlarmSet);
        if (actionMode != null) {
            actionMode.finish();
        }
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
    public void onSingleChoiceMemoClicked(ClickableMemo memo) {
        presenter.processSingleChoiceClick(memo, LinearLayoutManager.VERTICAL);
    }

    public void shutdownMemoAlarm(int position) {
        presenter.processMemoAlarmShutdown(mAdapter.getMemos().get(position));
        mAdapter.notifyItemChanged(position);
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
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

        void onEnterEditMode(ClickableMemo memo);
    }
}
