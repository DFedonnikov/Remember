package com.gnest.remember.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    private boolean isMultiSelectEnabled = false;
    private OnItemActionPerformed mListener;

    public MySelectableAdapter(List<SelectableMemo> memos, OnItemActionPerformed listener, boolean isMultiSelectEnabled) {
        mMemos = memos;
        mListener = listener;
        this.isMultiSelectEnabled = isMultiSelectEnabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new SelectableViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        SelectableViewHolder holder = (SelectableViewHolder) viewHolder;
        SelectableMemo selectableMemo = mMemos.get(position);
        String memoText = selectableMemo.getMemoText();
        holder.mMemo = selectableMemo;
        holder.mTextView.setText(memoText);
        holder.setChecked(holder.mMemo.isSelected());
    }

    @Override
    public int getItemCount() {
        return mMemos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMultiSelectEnabled) {
            return SelectableViewHolder.MULTI_SELECTION;
        } else {
            return SelectableViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(SelectableMemo memo, View view) {
        if (!isMultiSelectEnabled) {
            for (SelectableMemo selectableMemo : mMemos) {
                if (!selectableMemo.equals(memo) && selectableMemo.isSelected()) {
                    selectableMemo.setSelected(false);
                } else if (selectableMemo.equals(memo) && memo.isSelected()) {
                    selectableMemo.setSelected(true);
                }
            }
            notifyDataSetChanged();
        }
        mListener.onItemSelected(memo, view);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        mListener.onUpdateDBUponElementsSwap(mMemos.get(fromPosition), mMemos.get(toPosition));
//        Collections.swap(mMemos, fromPosition, toPosition);
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

    public interface OnItemActionPerformed {
        void onItemSelected(SelectableMemo memo, View view);
        void onUpdateDBUponSwipeDismiss(SelectableMemo memoToDelete, List<SelectableMemo> mMemos, int position);
        void onUpdateDBUponElementsSwap(SelectableMemo from, SelectableMemo to);
    }
}
