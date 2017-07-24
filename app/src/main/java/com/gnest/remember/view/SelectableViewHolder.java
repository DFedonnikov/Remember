package com.gnest.remember.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.helper.ItemTouchHelperViewHolder;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public static final int TV_PADDING_TOP_EXTENDED = 70;
    public static final int TV_PADDING_TOP_UNEXTENDED = 30;

    private OnItemSelectedListener mListener;
    private int mPosition = 0;
    private TextView mTextView;
    private SelectableMemo mMemo;
    private int textViewBackgroundId;
    private int textViewBackgroundSelectedId;
    ImageView pin;

    SelectableViewHolder(final View itemView, final OnItemSelectedListener onItemSelectedListener) {
        super(itemView);
        this.mListener = onItemSelectedListener;
        mTextView = itemView.findViewById(R.id.memo_textView);
        pin = itemView.findViewById(R.id.pin);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mPosition, mMemo, mTextView);
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
            setSelectedBackground();
        } else {
            setDeselectedBackground();
        }
        mMemo.setSelected(value);
    }

    @Override
    public void setSelectedBackground() {
        mTextView.setBackground(ContextCompat.getDrawable(mTextView.getContext(), textViewBackgroundSelectedId));
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedBackground() {
        mTextView.setBackground(ContextCompat.getDrawable(mTextView.getContext(), textViewBackgroundId));
        pin.setVisibility(View.VISIBLE);
    }

    void bind(SelectableMemo memo, int position) {
        String memoText = memo.getMemoText();
        mMemo = memo;
        mPosition = position;
        mTextView.setText(memoText);
        textViewBackgroundId = memo.getTextViewBackgroundId();
        textViewBackgroundSelectedId = memo.getTextViewBackgroundSelectedId();
        setChecked(memo.isSelected());
    }

    interface OnItemSelectedListener {
        void onItemClicked(int mPosition, SelectableMemo mMemo, TextView mTextView);

        boolean onItemLongClicked(int mPosition, SelectableMemo mMemo);

        boolean isMultiChoiceEnabled();

        void updateSelectedList(int pos, SelectableMemo memo);

    }

}

