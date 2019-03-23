package com.gnest.remember.view.adapters;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.gnest.remember.view.helper.ItemTouchHelperAdapter;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmResults;

public class MySelectableAdapter extends RecyclerView.Adapter<SelectableViewHolder> implements SelectableViewHolder.OnItemSelectedListener, ItemTouchHelperAdapter, MyGridLayoutManager.ExpandListener {

    private RealmResults<Memo> mMemos;
    private boolean isMultiChoiceEnabled = false;
    private OnItemActionPerformed mListener;
    private Map<Integer, Integer> mSelectedMap = new LinkedHashMap<>();
    private boolean isItemsExpanded;
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
        return new SelectableViewHolder(view, new WeakReference<>(this));
    }

    @Override
    public void onBindViewHolder(SelectableViewHolder viewHolder, int position) {
        final SelectableViewHolder holder = viewHolder;
        boolean isSelected = isMultiChoiceEnabled && mSelectedMap.get(position) != null;
        holder.bind(mMemos.get(position), position, isItemsExpanded, isSelected, mMemoSize, mMargins);
        holder.pin.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                new WeakReference<>(mListener).get().onStartDrag(holder);
            }
            return view.performClick();
        });
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
        mListener.onPerformSwipeDismiss(memo.getId(), memo.getPosition());
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return isMultiChoiceEnabled;
    }

    @Override
    public void updateSelectedList(int pos, Memo memo, SelectableViewHolder viewHolder) {
        if (mSelectedMap.get(pos) != null) {
            mSelectedMap.remove(pos);
            viewHolder.setDeselectedState();
        } else {
            mSelectedMap.put(pos, memo.getId());
            viewHolder.setSelectedState();
        }
        if (mSelectedMap.size() == 0) {
            mListener.shutDownActionMode();
            viewHolder.setDeselectedState();
        }
        mListener.updateContextActionMenu(mSelectedMap.size());
    }

    public void switchMultiSelect(boolean switchedOn) {
        isMultiChoiceEnabled = switchedOn;
    }

    public void clearSelectedList() {
        mSelectedMap.clear();
        notifyDataSetChanged();
    }

    public Collection<Integer> getSelectedIds() {
        return mSelectedMap.values();
    }

    @Override
    public void onItemClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder) {
        if (isMultiChoiceEnabled()) {
            updateSelectedList(mPosition, mMemo, viewHolder);
            if (mSelectedMap.size() < 2) {
                mListener.setShareButtonVisibility(true);
            } else {
                mListener.setShareButtonVisibility(false);
            }
        } else {
            mListener.onSingleChoiceMemoClicked(mMemo);
        }
    }

    @Override
    public boolean onItemLongClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder) {
        if (!isMultiChoiceEnabled() && !isItemsExpanded) {
            mListener.showActionMode();
            updateSelectedList(mMemo.getPosition(), mMemo, viewHolder);
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
        return isItemsExpanded;
    }

    @Override
    public void expandItems() {
        setItemsExpanded(true);
    }

    public void setItemsExpanded(boolean itemsExpanded) {
        this.isItemsExpanded = itemsExpanded;
        notifyDataSetChanged();
    }

    public interface OnItemActionPerformed {
        void onPerformSwipeDismiss(int memoId, int memoPosition);

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

        void updateContextActionMenu(int numOfSelectedItems);
    }
}
