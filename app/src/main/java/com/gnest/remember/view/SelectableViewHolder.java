package com.gnest.remember.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.data.ClickableMemo;
import com.gnest.remember.helper.ItemTouchHelperViewHolder;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private View mView;
    private OnItemSelectedListener mListener;
    private int mPosition = 0;
    private TextView mTextView;
    private ClickableMemo mMemo;
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
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mPosition, mMemo);
            }
        });
        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mListener.onItemLongClicked(mPosition, mMemo);
            }
        });
    }

    void setChecked(boolean value) {
        if (value) {
            setSelectedBackground();
        } else {
            setDeselectedBackground();
        }
        mMemo.setSelected(value);
    }

    @Override
    public void setSelectedBackground() {
        mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundSelectedId));
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedBackground() {
        if (mMemo.isExpanded()) {
            mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundExpandedId));
            pin.setVisibility(View.INVISIBLE);
        } else {
            mView.setBackground(ContextCompat.getDrawable(mView.getContext(), mTextViewBackgroundId));
            pin.setVisibility(View.VISIBLE);
        }

    }

    void bind(ClickableMemo memo, int position, boolean isExpanded) {
        String memoText = memo.getMemoText();
        mMemo = memo;
        mMemo.setPosition(position);
        mMemo.setExpanded(isExpanded);
        mPosition = position;
        mTextView.setText(memoText);
        ColorSpinnerAdapter.Colors color = ColorSpinnerAdapter.Colors.valueOf(memo.getColor());
        mTextViewBackgroundId = color.getMemoBackgroundId();
        mTextViewBackgroundSelectedId = color.getMemoBackgroundSelectedId();
        mTextViewBackgroundExpandedId = color.getMemoBackgroundExpandedId();
        setChecked(memo.isSelected());
    }

    interface OnItemSelectedListener {
        void onItemClicked(int mPosition, ClickableMemo mMemo);

        boolean onItemLongClicked(int mPosition, ClickableMemo mMemo);

        boolean isMultiChoiceEnabled();

        void updateSelectedList(int pos, ClickableMemo memo);

    }

}

