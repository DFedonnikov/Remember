package com.gnest.remember.view.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gnest.remember.R;

import java.lang.ref.WeakReference;

public class ColorSpinnerAdapter implements SpinnerAdapter {

    public enum Colors {
        YELLOW(R.color.memo_yellow, R.drawable.textview_background_yellow, R.drawable.textview_background_select_yellow, R.drawable.textview_background_yellow_expanded),
        BLUE(R.color.memo_blue, R.drawable.textview_background_blue, R.drawable.textview_background_select_blue, R.drawable.textview_background_blue_expanded),
        EMERALD(R.color.memo_emerald, R.drawable.textview_background_emerald, R.drawable.textview_background_select_emerald, R.drawable.textview_background_emerald_expanded),
        PURPLE(R.color.memo_purple, R.drawable.textview_background_purple, R.drawable.textview_background_select_purple, R.drawable.textview_background_purple_expanded);

        private int mColorId;
        private int mMemoBackgroundId;
        private int mMemoBackgroundSelectedId;
        private int mMemoBackgroundExpandedId;

        Colors(int colorId, int memoBackgroundId, int memoBackgroundSelectedId, int memoBackgroundExpandedId) {
            this.mColorId = colorId;
            this.mMemoBackgroundId = memoBackgroundId;
            this.mMemoBackgroundSelectedId = memoBackgroundSelectedId;
            this.mMemoBackgroundExpandedId = memoBackgroundExpandedId;
        }

        public int getColorId() {
            return mColorId;
        }

        public int getMemoBackgroundId() {
            return mMemoBackgroundId;
        }

        public int getMemoBackgroundSelectedId() {
            return mMemoBackgroundSelectedId;
        }

        public int getMemoBackgroundExpandedId() {
            return mMemoBackgroundExpandedId;
        }

        @Override
        public String toString() {
            return this.name();
        }
    }

    private String[] mColors;
    private WeakReference<Context> mContext;

    public ColorSpinnerAdapter(Context context) {
        this.mContext = new WeakReference<>(context);
        mColors = mContext.get().getResources().getStringArray(R.array.memo_colors);
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext.get());
            view = inflater.inflate(R.layout.color_spinner_list_item, viewGroup, false);
        }
        TextView colorDesc = view.findViewById(R.id.color_text_list_item);
        colorDesc.setText(mColors[i]);
        ImageView colorImage = view.findViewById(R.id.color_image_list_item);
        GradientDrawable gd = (GradientDrawable) colorImage.getDrawable();
        gd.setColor(ContextCompat.getColor(mContext.get(), Colors.values()[i].getColorId()));
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        //No implementation needed
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        //No implementation needed
    }

    @Override
    public int getCount() {
        return mColors.length;
    }

    @Override
    public Object getItem(int i) {
        return mColors[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext.get());
            view = inflater.inflate(R.layout.color_spinner_selected_item, viewGroup, false);
        }
        ImageView colorSelectedImage = view.findViewById(R.id.color_image_selected_item);
        GradientDrawable gd = (GradientDrawable) colorSelectedImage.getDrawable();
        gd.setColor(ContextCompat.getColor(mContext.get(), Colors.values()[i].getColorId()));
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
