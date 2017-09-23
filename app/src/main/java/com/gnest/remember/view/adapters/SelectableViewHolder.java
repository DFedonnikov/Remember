package com.gnest.remember.view.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.helper.ItemTouchHelperViewHolder;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private View mView;
    private OnItemSelectedListener mListener;
    private int mPosition = 0;
    private TextView mTextView;
    private Memo mMemo;
    private ScrollView mScrollView;
    private int mTextViewBackgroundId;
    private int mTextViewBackgroundSelectedId;
    private int mTextViewBackgroundExpandedId;
    ImageView pin;

    SelectableViewHolder(final View itemView, final OnItemSelectedListener onItemSelectedListener) {
        super(itemView);
        mView = itemView;
        this.mListener = onItemSelectedListener;
        mTextView = itemView.findViewById(R.id.memo_textView);
        pin = itemView.findViewById(R.id.pin);
        mScrollView = itemView.findViewById(R.id.textScrollView);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mPosition, mMemo);
            }
        });
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mListener.onItemLongClicked(mPosition, mMemo);
            }
        });
    }

    void setChecked(boolean value) {
        if (value) {
            setSelectedState();
        } else {
            setDeselectedState();
        }
//        mMemo.setSelected(value);
    }

    @Override
    public void setSelectedState() {
        mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundSelectedId));
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedState() {
        if (mMemo.isExpanded()) {
            mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundExpandedId));
            pin.setVisibility(View.INVISIBLE);
            mTextView.setMaxLines(Integer.MAX_VALUE);
            mScrollView.setEnabled(true);
        } else {
            mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundId));
            pin.setVisibility(View.VISIBLE);
            mTextView.setMaxLines(5);
            mScrollView.setEnabled(false);
        }

    }

    void bind(Memo memo, int position, boolean isExpanded) {
        String memoText = memo.getMemoText();
        mMemo = memo;
//        mMemo.setPosition(position);
//        mMemo.setExpanded(isExpanded);
        mPosition = position;
        mTextView.setText(memoText);
        ColorSpinnerAdapter.Colors color = ColorSpinnerAdapter.Colors.valueOf(memo.getColor());
        mTextViewBackgroundId = color.getMemoBackgroundId();
        mTextViewBackgroundSelectedId = color.getMemoBackgroundSelectedId();
        mTextViewBackgroundExpandedId = color.getMemoBackgroundExpandedId();
        setChecked(memo.isSelected());
    }

    interface OnItemSelectedListener {
        void onItemClicked(int mPosition, Memo mMemo);

        boolean onItemLongClicked(int mPosition, Memo mMemo);

        boolean isMultiChoiceEnabled();

        void updateSelectedList(int pos, Memo memo);

    }

}

