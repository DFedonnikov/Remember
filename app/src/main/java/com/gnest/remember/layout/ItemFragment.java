package com.gnest.remember.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;

import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.db.DatabaseAccess;
import com.gnest.remember.helper.ItemTouchHelperCallback;
import com.gnest.remember.view.ActionMenu;
import com.gnest.remember.view.MySelectableAdapter;

import java.util.List;


public class ItemFragment extends Fragment implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount;
    private OnItemListFragmentInteractionListener mListener;
    private MySelectableAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private DatabaseAccess databaseAccess;
    private List<SelectableMemo> memos;
    private ActionMode actionMode;
    private ActionMenu actionMenu;
    private SelectableMemo currentSelectedMemo;
    private RecyclerView recyclerView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.databaseAccess = DatabaseAccess.getInstance(this.getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        actionMenu = new ActionMenu(this);

        databaseAccess.open();
        this.memos = databaseAccess.getAllMemos();
        databaseAccess.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        recyclerView = view.findViewById(R.id.memo_list);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MySelectableAdapter(memos, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_menu, menu);
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
    public void onEditButtonPressed() {
        SparseArray<SelectableMemo> selectedList = adapter.getmSelectedList();
        if (selectedList.size() == 1) {
            currentSelectedMemo = selectedList.valueAt(0);
            mListener.onEditButtonPressed(currentSelectedMemo);
            actionMode.finish();
        }
    }

    @Override
    public void onDeleteButtonPressed() {
        SparseArray<SelectableMemo> selectedList = adapter.getmSelectedList();
        for (int i = 0; i < selectedList.size(); i++) {
            currentSelectedMemo = selectedList.valueAt(i);
            adapter.removeSelectedMemo(currentSelectedMemo);
            deleteCurrentMemoFromDB(memos, currentSelectedMemo.getPosition());
        }
        adapter.notifyDataSetChanged();
        actionMode.finish();
    }

    @Override
    public void onShareButtonPressed() {
        SparseArray<SelectableMemo> selectedList = adapter.getmSelectedList();
        if (selectedList.size() == 1) {
            currentSelectedMemo = selectedList.valueAt(0);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, currentSelectedMemo.getMemoText());
            Intent chooserIntent = Intent.createChooser(intent, "Send Memo...");
            startActivity(chooserIntent);
            actionMode.finish();
        }


    }


    private void deleteCurrentMemoFromDB(List<SelectableMemo> mMemos, int position) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
        databaseAccess.open();
        if (currentSelectedMemo != null) {
            databaseAccess.delete(currentSelectedMemo, mMemos, position);
        }
        databaseAccess.close();
    }

    @Override
    public void onUpdateDBUponSwipeDismiss(SelectableMemo memoToDelete, List<SelectableMemo> mMemos, int position) {
        currentSelectedMemo = memoToDelete;
        deleteCurrentMemoFromDB(mMemos, position);
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onUpdateDBUponElementsSwap(SelectableMemo from, SelectableMemo to) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
        databaseAccess.open();
        databaseAccess.swapMemos(from, to);
        databaseAccess.close();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void showActionMode() {
        if (actionMenu != null) {
            actionMode = getActivity().startActionMode(actionMenu);
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
    public void setEditAndShareButtonVisibility(boolean isVisible) {
        if (actionMenu != null) {
            actionMenu.setEditAndShareButtonVisibility(isVisible);
        }
    }

    @Override
    public void onItemEditButtonClicker(SelectableMemo mMemo) {
        mListener.onEditButtonPressed(mMemo);
    }

    public void setmColumnCount(int mColumnCount) {
        this.mColumnCount = mColumnCount;
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

        void onEditButtonPressed(SelectableMemo memo);


    }
}
