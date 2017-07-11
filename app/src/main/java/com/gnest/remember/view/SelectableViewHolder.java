package com.gnest.remember.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnest.remember.R;
import com.gnest.remember.data.SelectableMemo;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class SelectableViewHolder extends RecyclerView.ViewHolder {
    static final int MULTI_SELECTION = 2;
    static final int SINGLE_SELECTION = 1;

    private View mView;
    private ImageView mImageView;
    private OnItemSelectedListener mListener;
    TextView mTextView;
    SelectableMemo mMemo;

    SelectableViewHolder(View itemView, final OnItemSelectedListener onItemSelectedListener) {
        super(itemView);
        this.mView = itemView;
        this.mListener = onItemSelectedListener;
        mTextView = itemView.findViewById(R.id.memo_textView);
        mImageView = itemView.findViewById(R.id.stick_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
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
            mImageView.setBackground(ContextCompat.getDrawable(mImageView.getContext(), R.drawable.note_select));
        } else {
            mImageView.setBackground(ContextCompat.getDrawable(mImageView.getContext(), R.drawable.sticky_note));
        }
        mMemo.setSelected(value);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(SelectableMemo memo, View view);


    }

}

