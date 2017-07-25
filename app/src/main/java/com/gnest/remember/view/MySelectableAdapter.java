package com.gnest.remember.view;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
    private SparseArray<SelectableMemo> mSelectedList = new SparseArray<>();

    public MySelectableAdapter(List<SelectableMemo> memos, OnItemActionPerformed listener) {
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
        SelectableMemo memoFrom = mMemos.get(from);
        SelectableMemo memoTo = mMemos.get(to);
        mListener.onUpdateDBUponElementsSwap(memoFrom, memoTo);
        memoTo.setPosition(from);
        memoFrom.setPosition(to);
        Collections.swap(mMemos, from, to);
    }

    @Override
    public void onItemDismiss(int position) {
        mListener.onPerformSwipeDismiss(mMemos.get(position));
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return multiChoiceEnabled;
    }

    @Override
    public void updateSelectedList(int pos, SelectableMemo memo) {
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

    public void removeSelectedMemo(SelectableMemo memeToRemove) {
        mMemos.remove(memeToRemove);
    }

    public SparseArray<SelectableMemo> getmSelectedList() {
        return mSelectedList;
    }

    @Override
    public void onItemClicked(int mPosition, SelectableMemo mMemo) {
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
    public boolean onItemLongClicked(int mPosition, SelectableMemo mMemo) {
        if (!isMultiChoiceEnabled()) {
            updateSelectedList(mMemo.getPosition(), mMemo);
            mListener.showActionMode();
            return true;
        }
        return false;
    }

    public void setmMemos(List<SelectableMemo> mMemos) {
        this.mMemos = mMemos;
    }

    public interface OnItemActionPerformed {
        void onPerformSwipeDismiss(SelectableMemo memoToDelete);
        void onUpdateDBUponElementsSwap(SelectableMemo from, SelectableMemo to);
        void showActionMode();
        void shutDownActionMode();
        void setShareButtonVisibility(boolean isVisible);

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);

        void onSingleChoiceMemoClicked(SelectableMemo mMemo);
    }
}
