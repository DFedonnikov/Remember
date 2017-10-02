package com.gnest.remember.view.adapters;

import android.support.v4.util.Pair;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.gnest.remember.view.helper.ItemTouchHelperAdapter;

import io.realm.RealmResults;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class MySelectableAdapter extends RecyclerView.Adapter<SelectableViewHolder> implements SelectableViewHolder.OnItemSelectedListener, ItemTouchHelperAdapter, MyGridLayoutManager.ExpandListener {

    private RealmResults<Memo> mMemos;
    private boolean multiChoiceEnabled = false;
    private OnItemActionPerformed mListener;
    private SparseArray<Pair<Integer, Boolean>> mSelectedList = new SparseArray<>();
    private boolean mItemsExpanded;
    private int mMemoSize;
    private int mMargins;

    public MySelectableAdapter(int memoSize, int margins) {
        this.mMemoSize = memoSize;
        this.mMargins = margins;
    }

    public void setActionListener(OnItemActionPerformed mListener) {
        this.mListener = mListener;
    }

    @Override
    public SelectableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new SelectableViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(SelectableViewHolder viewHolder, int position) {
        final SelectableViewHolder holder = viewHolder;
        holder.bind(mMemos.get(position), position, mItemsExpanded, mMemoSize, mMargins);
        holder.pin.setOnTouchListener((view, motionEvent) -> {
            if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                mListener.onStartDrag(holder);
            }
            return false;
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
        Memo memoFrom = mMemos.get(from);
        Memo memoTo = mMemos.get(to);
        mListener.swapMemos(memoFrom.getId(), memoFrom.getPosition(), memoTo.getId(), memoTo.getPosition());
    }

    @Override
    public void onItemDismiss(int position) {
        Memo memo = mMemos.get(position);
        mListener.onPerformSwipeDismiss(memo.getId(), memo.getPosition(), memo.isAlarmSet());
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return multiChoiceEnabled;
    }

    @Override
    public void updateSelectedList(int pos, Memo memo) {
        if (mSelectedList.indexOfKey(pos) >= 0) {
            mSelectedList.delete(pos);
        } else {
            mSelectedList.put(pos, new Pair<>(memo.getId(), memo.isAlarmSet()));
        }
        if (mSelectedList.size() == 0) {
            mListener.shutDownActionMode();
        }
        notifyDataSetChanged();
    }

    public void switchMultiSelect(boolean switchedOn) {
        multiChoiceEnabled = switchedOn;
    }

    public void clearSelectedList() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public SparseArray<Pair<Integer, Boolean>> getSelectedList() {
        return mSelectedList;
    }

    @Override
    public void onItemClicked(int mPosition, Memo mMemo) {
        if (isMultiChoiceEnabled()) {
            updateSelectedList(mPosition, mMemo);
            if (mSelectedList.size() < 2) {
                mListener.setShareButtonVisibility(true);
            } else {
                mListener.setShareButtonVisibility(false);
            }
        } else {
            mListener.onSingleChoiceMemoClicked(mMemo);
        }
    }

    @Override
    public boolean onItemLongClicked(int mPosition, Memo mMemo) {
        if (!isMultiChoiceEnabled() && !mItemsExpanded) {
            updateSelectedList(mMemo.getPosition(), mMemo);
            mListener.showActionMode();
            return true;
        }
        return false;
    }

    public RealmResults<Memo> getMemos() {
        return mMemos;
    }

    public void setMemos(RealmResults<Memo> memos) {
        this.mMemos = memos;
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

        void onSingleChoiceMemoClicked(Memo mMemo);
    }
}
