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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.db.DatabaseAccess;
import com.gnest.remember.presenter.IPresenter;
import com.gnest.remember.presenter.Presenter;
import com.gnest.remember.view.IView;
import com.gnest.remember.view.helper.ItemTouchHelperCallback;
import com.gnest.remember.model.services.AlarmService;
import com.gnest.remember.view.ActionMenu;
import com.gnest.remember.view.MyGridLayoutManager;
import com.gnest.remember.view.MySelectableAdapter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;


public class ListItemFragment extends MvpFragment<IView, IPresenter>
        implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, IView {

    private static final String ARG_COLUMN_COUNT = "ColumnCount";

    public static final int LM_HORIZONTAL_ORIENTATION = 0;
    public static final int LM_VERTICAL_ORIENTATION = 1;
    public static final String LM_SCROLL_ORIENTATION_KEY = "LayoutManagerOrientationKey";
    public static final String POSITION_KEY = "POSITION_KEY";
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String EXPANDED_KEY = "EXPANDED_KEY";

    private int mColumnCount;
    private OnItemListFragmentInteractionListener mListener;
    private MySelectableAdapter adapter;
    private View mView;
    private ItemTouchHelper itemTouchHelper;
    private DatabaseAccess databaseAccess;
    private List<ClickableMemo> memos;
    private android.support.v7.view.ActionMode actionMode;
    private ActionMenu actionMenu;
    private RecyclerView recyclerView;
    private MyGridLayoutManager myGridLayoutManager;
    private int lastPosition;
    private int lastOrientation;

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
//        this.databaseAccess = DatabaseAccess.getInstance(this.getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        actionMenu = new ActionMenu(this);
//        databaseAccess.open();

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
        recyclerView = mView.findViewById(R.id.memo_list);
        Context context = mView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            myGridLayoutManager = new MyGridLayoutManager(context, mColumnCount);
            recyclerView.setLayoutManager(myGridLayoutManager);
        }
        adapter = new MySelectableAdapter(memos, this);
        myGridLayoutManager.setExpandListener(adapter);



        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            Bundle bundle = getArguments().getBundle(BUNDLE_KEY);
            if (bundle != null) {
                lastOrientation = bundle.getInt(LM_SCROLL_ORIENTATION_KEY);
                lastPosition = bundle.getInt(POSITION_KEY);
                myGridLayoutManager.setmAncorPos(lastPosition);
                myGridLayoutManager.setOrientation(lastOrientation);
                adapter.setItemsExpanded(bundle.getBoolean(EXPANDED_KEY));
            }
        }
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
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
        if (context instanceof OnItemListFragmentInteractionListener) {
            mListener = (OnItemListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemListFragmentInteractionListener");
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
        memos = null;
    }


    @Override
    @NonNull
    public IPresenter createPresenter() {
        return new Presenter(getActivity().getSupportLoaderManager());
    }


    @Override
    public void setData(List<ClickableMemo> data) {
        adapter.setMemos(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = getArguments().getBundle(BUNDLE_KEY);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(LM_SCROLL_ORIENTATION_KEY, myGridLayoutManager.getOrientation());
        bundle.putInt(POSITION_KEY, myGridLayoutManager.getLastPosition());
        bundle.putBoolean(EXPANDED_KEY, adapter.isItemsExpanded());
        getArguments().putBundle(BUNDLE_KEY, bundle);
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
        presenter.deleteSelectedMemos(adapter.getSelectedList(), adapter.getMemos());
        adapter.removeSelectedMemos(adapter.getSelectedList());
        actionMode.finish();
    }

    @Override
    public void onPerformSwipeDismiss(int memoId, int memoPosition, boolean isAlarmSet) {
        presenter.deleteMemo(memoId, memoPosition, adapter.getMemos(), isAlarmSet);
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
        presenter.share(adapter.getSelectedList());
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
        presenter.proccessMemoSwap(fromId, fromPosition, toId, toPosition);
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
        adapter.switchMultiSelect(switchedOn);
    }

    @Override
    public void clearSelection() {
        adapter.clearSelectedList();
    }

    @Override
    public void setShareButtonVisibility(boolean isVisible) {
        if (actionMenu != null) {
            actionMenu.setShareButtonVisibility(isVisible);
        }
    }

    @Override
    public void onSingleChoiceMemoClicked(ClickableMemo mMemo) {
        if (myGridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            openClickedItem(mMemo.getPosition());
        } else {
            mListener.onEnterEditMode(mMemo);
        }
    }

    public void openClickedItem(int position) {
        myGridLayoutManager.openItem(position);
    }

    public void updateMemoAlarm(int position) {
        ClickableMemo memo = memos.get(position);
        databaseAccess.setMemoAlarmFalse(memo.getId());
        memo.setAlarmSet(false);
        adapter.notifyItemChanged(position);
    }
    
    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
    }

    public void onBackButtonPressed() {
        if (myGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            myGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter.setItemsExpanded(false);
            databaseAccess.startUpdateExpandedColumnTask(adapter.isItemsExpanded());
        } else {
            mListener.onBackButtonPressed();
        }
    }

    public MySelectableAdapter getAdapter() {
        return adapter;
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
    public interface OnItemListFragmentInteractionListener {
        void onAddButtonPressed();

        void onBackButtonPressed();

        void onEnterEditMode(ClickableMemo memo);
    }
}
