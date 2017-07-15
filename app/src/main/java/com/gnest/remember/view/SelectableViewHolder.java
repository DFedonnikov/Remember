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

class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    static final int MULTI_SELECTION = 2;
    static final int SINGLE_SELECTION = 1;

    private View mView;
    private OnItemSelectedListener mListener;

    ImageView pin;
    TextView mTextView;
    SelectableMemo mMemo;

    SelectableViewHolder(View itemView, final OnItemSelectedListener onItemSelectedListener) {
        super(itemView);
        this.mView = itemView;
        this.mListener = onItemSelectedListener;
        mTextView = itemView.findViewById(R.id.memo_textView);
        pin = itemView.findViewById(R.id.pin);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMemo.isSelected()) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                mListener.onItemSelected(mMemo, view);
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
        mTextView.setBackground(ContextCompat.getDrawable(mTextView.getContext(), R.drawable.note_select));
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedBackground() {
        mTextView.setBackground(ContextCompat.getDrawable(mTextView.getContext(), R.drawable.sticky_note));
        pin.setVisibility(View.VISIBLE);
    }

    interface OnItemSelectedListener {
        void onItemSelected(SelectableMemo memo, View view);


    }

}

