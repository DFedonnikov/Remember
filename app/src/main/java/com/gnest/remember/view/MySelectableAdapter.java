package com.gnest.remember.view;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.helper.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class MySelectableAdapter extends RecyclerView.Adapter implements SelectableViewHolder.OnItemSelectedListener, ItemTouchHelperAdapter, MyGridLayoutManager.ExpandListener {
    private List<ClickableMemo> mMemos;
    private boolean multiChoiceEnabled = false;
    private OnItemActionPerformed mListener;
    private SparseArray<ClickableMemo> mSelectedList = new SparseArray<>();
    private boolean mItemsExpanded;

    public MySelectableAdapter(List<ClickableMemo> memos, OnItemActionPerformed listener) {
        this.mMemos = memos;
        this.mListener = listener;
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
        holder.bind(mMemos.get(position), position, mItemsExpanded);
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
        return mMemos != null ? mMemos.size() : 0;
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
        ClickableMemo memoFrom = mMemos.get(from);
        ClickableMemo memoTo = mMemos.get(to);
        mListener.swapMemos(memoFrom.getId(), memoFrom.getPosition(), memoTo.getId(), memoTo.getPosition());
        memoTo.setPosition(from);
        memoFrom.setPosition(to);
        Collections.swap(mMemos, from, to);
    }

    @Override
    public void onItemDismiss(int position) {
        ClickableMemo memo = mMemos.get(position);
        mListener.onPerformSwipeDismiss(memo.getId(), memo.getPosition(), memo.isAlarmSet());
        mMemos.remove(memo);
        notifyItemRemoved(position);
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return multiChoiceEnabled;
    }

    @Override
    public void updateSelectedList(int pos, ClickableMemo memo) {
        if (mSelectedList.indexOfKey(pos) >= 0) {
            mSelectedList.delete(pos);
        } else {
            mSelectedList.put(pos, memo);
        }
        if (mSelectedList.size() == 0) {
            mListener.shutDownActionMode();
        }
        notifyDataSetChanged();
    }

//    @Override
//    public void showActionMode() {
//        mListener.showActionMode();
//    }

    public void switchMultiSelect(boolean switchedOn) {
        multiChoiceEnabled = switchedOn;
    }

    public void clearSelectedList() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public void removeSelectedMemos(SparseArray<ClickableMemo> memosToRemove) {
        for (int i = 0; i < memosToRemove.size(); i++) {
            mMemos.remove(memosToRemove.valueAt(i));
        }
        notifyDataSetChanged();
    }

    public SparseArray<ClickableMemo> getSelectedList() {
        return mSelectedList;
    }

    @Override
    public void onItemClicked(int mPosition, ClickableMemo mMemo) {
        if (isMultiChoiceEnabled()) {
            updateSelectedList(mPosition, mMemo);
            if (mSelectedList.size() < 2) {
                mListener.setShareButtonVisibility(true);
            } else {
                mListener.setShareButtonVisibility(false);
            }
        } else {
//            mListener.onEnterItemEditMode(mMemo);
            mListener.onSingleChoiceMemoClicked(mMemo);
        }
    }

    @Override
    public boolean onItemLongClicked(int mPosition, ClickableMemo mMemo) {
        if (!isMultiChoiceEnabled() && !mItemsExpanded) {
            updateSelectedList(mMemo.getPosition(), mMemo);
            mListener.showActionMode();
            return true;
        }
        return false;
    }

    public List<ClickableMemo> getMemos() {
        return mMemos;
    }

    public void setMemos(List<ClickableMemo> mMemos) {
        this.mMemos = mMemos;
    }

    public boolean isItemsExpanded() {
        return mItemsExpanded;
    }

    @Override
    public void expandItems() {
        setItemsExpanded(true);
    }

    public void setItemsExpanded(boolean itemsExpanded) {
        this.mItemsExpanded = itemsExpanded;
        notifyDataSetChanged();
    }

    public interface OnItemActionPerformed {
        void onPerformSwipeDismiss(int memoId, int memoPosition, boolean isAlarmSet);

        void swapMemos(int fromId, int fromPosition, int toId, int toPosition);

        void showActionMode();

        void shutDownActionMode();

        void setShareButtonVisibility(boolean isVisible);

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);

        void onSingleChoiceMemoClicked(ClickableMemo mMemo);
    }
}
