package com.gnest.remember.view;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.helper.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class MySelectableAdapter extends RecyclerView.Adapter implements SelectableViewHolder.OnItemSelectedListener, ItemTouchHelperAdapter {
    private List<SelectableMemo > mMemos;
    private boolean multiChoiceEnabled = false;
    private OnItemActionPerformed mListener;
    private SparseIntArray mSelectedList = new SparseIntArray();

    public MySelectableAdapter(List<SelectableMemo> memos, OnItemActionPerformed listener, boolean multiChoiceEnabled) {
        this.mMemos = memos;
        this.mListener = listener;
        this.multiChoiceEnabled = multiChoiceEnabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new SelectableViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final SelectableViewHolder holder = (SelectableViewHolder) viewHolder;
        holder.bind(mMemos.get(position), position);
        holder.pin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mListener.onStartDrag(holder);
                }
                return false;
            }
        });

        if (multiChoiceEnabled && mSelectedList.indexOfKey(position) >= 0) {
            holder.setChecked(true);
        } else {
            holder.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return mMemos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (multiChoiceEnabled) {
            return SelectableViewHolder.MULTI_SELECTION;
        } else {
            return SelectableViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(SelectableMemo memo, View view) {
        if (!multiChoiceEnabled) {
            for (SelectableMemo selectableMemo : mMemos) {
                if (!selectableMemo.equals(memo) && selectableMemo.isSelected()) {
                    selectableMemo.setSelected(false);
                } else if (selectableMemo.equals(memo) && memo.isSelected()) {
                    selectableMemo.setSelected(true);
                } else if (selectableMemo.equals(memo) && !memo.isSelected()) {
                    selectableMemo.setSelected(false);
                }
            }
        }
        notifyDataSetChanged();
        mListener.onItemSelected(memo, view);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                makeSwap(i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                makeSwap(i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private void makeSwap(int from, int to) {
        SelectableMemo memoFrom = mMemos.get(from);
        SelectableMemo memoTo = mMemos.get(to);
        mListener.onUpdateDBUponElementsSwap(memoFrom, memoTo);
        memoTo.setPosition(from);
        memoFrom.setPosition(to);
        Collections.swap(mMemos, from, to);
    }

    @Override
    public void onItemDismiss(int position) {
        SelectableMemo memoToDelete = mMemos.get(position);
        mListener.onUpdateDBUponSwipeDismiss(memoToDelete, mMemos, position);
        mMemos.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return multiChoiceEnabled;
    }

    @Override
    public void updateSelectedList(int pos) {
        if (mSelectedList.indexOfKey(pos) >= 0) {
            mSelectedList.delete(pos);
        } else {
            mSelectedList.put(pos, pos);
        }
        if (mSelectedList.size() == 0) {
            mListener.shutDownActionMode();
        }
        notifyDataSetChanged();
    }

    @Override
    public void showActionMode() {
        mListener.showActionMode();
    }

    public void switchMultiSelect(boolean switchedOn) {
        multiChoiceEnabled = switchedOn;
    }

    public void clearSelectedList() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemActionPerformed {
        void onItemSelected(SelectableMemo memo, View view);
        void onUpdateDBUponSwipeDismiss(SelectableMemo memoToDelete, List<SelectableMemo> mMemos, int position);
        void onUpdateDBUponElementsSwap(SelectableMemo from, SelectableMemo to);
        void showActionMode();
        void shutDownActionMode();

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }
}
