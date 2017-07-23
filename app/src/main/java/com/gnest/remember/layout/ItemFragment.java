package com.gnest.remember.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.gnest.remember.loader.DBLoader;
import com.gnest.remember.view.ActionMenu;
import com.gnest.remember.view.MyGridLayoutManager;
import com.gnest.remember.view.MySelectableAdapter;

import java.util.List;


public class ItemFragment extends Fragment implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.MenuInteractionHelper, LoaderManager.LoaderCallbacks<List<SelectableMemo>> {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int LOADER_ID = 0;
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
    private MyGridLayoutManager myGridLayoutManager;


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
            myGridLayoutManager = new MyGridLayoutManager(context, mColumnCount);
            recyclerView.setLayoutManager(myGridLayoutManager);
        }
        adapter = new MySelectableAdapter(memos, this);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
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
        databaseAccess.close();
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
        if (currentSelectedMemo != null) {
            databaseAccess.delete(currentSelectedMemo, mMemos, position);
        }
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
        databaseAccess.swapMemos(from, to);
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
    public void setShareButtonVisibility(boolean isVisible) {
        if (actionMenu != null) {
            actionMenu.setShareButtonVisibility(isVisible);
        }
    }

       @Override
    public void onSingleChoiceMemoClicked(SelectableMemo mMemo) {
        if (myGridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            myGridLayoutManager.openItem(mMemo.getPosition());
        } else {
            mListener.onEnterEditMode(mMemo);
        }
    }

    @Override
    public Loader<List<SelectableMemo>> onCreateLoader(int id, Bundle args) {
        return new DBLoader(this.getContext(), databaseAccess);
    }

    @Override
    public void onLoadFinished(Loader<List<SelectableMemo>> loader, List<SelectableMemo> data) {
        memos = data;
        adapter.setmMemos(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<SelectableMemo>> loader) {

    }

    public void setmColumnCount(int mColumnCount) {
        this.mColumnCount = mColumnCount;
    }

    public void onBackButtonPressed() {
        if (myGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            myGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }
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

        void onEnterEditMode(SelectableMemo memo);


    }
}
