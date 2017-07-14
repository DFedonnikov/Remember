package com.gnest.remember.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;
import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.db.DatabaseAccess;
import com.gnest.remember.helper.ItemTouchHelperCallback;
import com.gnest.remember.view.ActionMenu;
import com.gnest.remember.view.MySelectableAdapter;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements MySelectableAdapter.OnItemActionPerformed, ActionMenu.OnMenuItemClickedListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount;
    private OnItemListFragmentInteractionListener mListener;
    private MySelectableAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private DatabaseAccess databaseAccess;
    private List<SelectableMemo> memos;
    private ActionMode actionMode;
    private ActionMenu currentMenu;
    private SelectableMemo currentSelectedMemo;
    private View currentSelectedView;
    private RecyclerView recyclerView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @SuppressWarnings("unused")
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
        adapter = new MySelectableAdapter(memos, this, false);
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
    public void onItemSelected(SelectableMemo memo, View view) {
        currentSelectedMemo = memo;
        currentSelectedView = view;
        if (actionMode == null) {
            currentMenu = new ActionMenu(this);
            actionMode = getActivity().startActionMode(currentMenu);
        }
        if (!memo.isSelected()) {
            actionMode.finish();
            actionMode = null;
        }
    }

    @Override
    public void onDeselectMemo() {
        if (currentSelectedMemo.isSelected()) {
            currentSelectedMemo.setSelected(false);
            adapter.onItemSelected(currentSelectedMemo, currentSelectedView);
        }
    }

    @Override
    public void onEditButtonPressed() {
        mListener.onEditButtonPressed(currentSelectedMemo);
        actionMode.finish();
    }

    @Override
    public void onDeleteButtonPressed() {
        memos.remove(currentSelectedMemo.getPosition());
        adapter.notifyItemRemoved(currentSelectedMemo.getPosition());
        deleteCurrentMemoFromDB(memos, currentSelectedMemo.getPosition());
        actionMode.finish();
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
    }

    @Override
    public void onUpdateDBUponElementsSwap(SelectableMemo from, SelectableMemo to) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.getContext());
        databaseAccess.open();
        databaseAccess.swapMemos(from, to);
        databaseAccess.close();
    }

    @Override
    public void onShareButtonPressed() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, currentSelectedMemo.getMemoText());
        Intent chooserIntent = Intent.createChooser(intent, "Send Memo...");
        startActivity(chooserIntent);
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
