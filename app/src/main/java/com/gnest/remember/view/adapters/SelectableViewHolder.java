package com.gnest.remember.view.adapters;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.helper.ItemTouchHelperViewHolder;

class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private View mView;
    private OnItemSelectedListener mListener;
    private Memo mMemo;
    private int mPosition = 0;
    private TextView mTextView;
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
        mTextView.setOnClickListener(view -> mListener.onItemClicked(mPosition, mMemo, SelectableViewHolder.this));
        mTextView.setOnLongClickListener(view -> mListener.onItemLongClicked(mPosition, mMemo, SelectableViewHolder.this));
    }

    @Override
    public void setSelectedState() {
        mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundSelectedId));
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedState() {
        mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundId));
        pin.setVisibility(View.VISIBLE);
    }

    private void setDeselectedAndExpandedState() {
        mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundExpandedId));
        pin.setVisibility(View.INVISIBLE);
    }

    void bind(Memo memo, int position, boolean isExpanded, boolean isSelected, int memoSize, int margins) {
        mMemo = memo;
        mPosition = position;
        setUpViewSizesAndMargins(memoSize, margins);
        setUpBackgroundColors(memo.getColor());
        setUpTextField(memo.getMemoText(), isExpanded, isSelected);
    }

    private void setUpViewSizesAndMargins(int memoSize, int margins) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(memoSize, memoSize);
        layoutParams.setMargins(margins, margins, margins, margins);
        mView.setLayoutParams(layoutParams);
    }

    private void setUpBackgroundColors(String memoColor) {
        ColorSpinnerAdapter.Colors color = ColorSpinnerAdapter.Colors.valueOf(memoColor);
        mTextViewBackgroundId = color.getMemoBackgroundId();
        mTextViewBackgroundSelectedId = color.getMemoBackgroundSelectedId();
        mTextViewBackgroundExpandedId = color.getMemoBackgroundExpandedId();
    }

    private void setUpTextField(String memoText, boolean isExpanded, boolean isSelected) {
        mTextView.setText(memoText);
        if (isExpanded) {
            setDeselectedAndExpandedState();
            mTextView.setMaxLines(Integer.MAX_VALUE);
            mScrollView.setEnabled(true);
        } else {
            if (isSelected) {
                setSelectedState();
            } else {
                setDeselectedState();
            }
            mTextView.setMaxLines(5);
            mScrollView.setEnabled(false);
        }
    }

    interface OnItemSelectedListener {
        void onItemClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder);

        boolean onItemLongClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder);

        boolean isMultiChoiceEnabled();

        void updateSelectedList(int pos, Memo memo, SelectableViewHolder viewHolder);

    }

}

