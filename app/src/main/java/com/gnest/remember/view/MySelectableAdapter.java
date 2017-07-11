package com.gnest.remember.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gnest.remember.R;
import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class MySelectableAdapter extends RecyclerView.Adapter implements SelectableViewHolder.OnItemSelectedListener {
    private List<SelectableMemo > mMemos;
    private boolean isMultiSelectEnabled = false;
    private SelectableViewHolder.OnItemSelectedListener mListener;

    public MySelectableAdapter(List<Memo> memos, SelectableViewHolder.OnItemSelectedListener listener, boolean isMultiSelectEnabled) {
        mMemos = new ArrayList<>();
        for (Memo memo : memos) {
            mMemos.add(new SelectableMemo(memo, false));
        }
        mListener = listener;
        this.isMultiSelectEnabled = isMultiSelectEnabled;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new SelectableViewHolder(view, (FrameLayout) view.findViewById(R.id.item_frame_layout), this);
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
}
