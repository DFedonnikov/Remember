package com.gnest.remember.ui.adapters;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gnest.remember.App;
import com.gnest.remember.R;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.ui.helper.ItemTouchHelperViewHolder;

import java.lang.ref.WeakReference;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

class SelectableViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private View mView;
    private WeakReference<OnItemSelectedListener> mListener;
    private Memo mMemo;
    private int mPosition = 0;
    private int mBackgroundId;
    private int mBackgroundSelectedId;
    private int mBackgroundExpandedId;
    private SimpleTarget<Drawable> mBackgroundTarget;

    @BindView(R.id.memo_textView)
    TextView textView;
    @BindView(R.id.textScrollView)
    ScrollView scrollView;
    @BindView(R.id.pin)
    ImageView pin;

    SelectableViewHolder(final View itemView, final WeakReference<OnItemSelectedListener> onItemSelectedListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mView = itemView;
        this.mListener = onItemSelectedListener;
        this.mBackgroundTarget = getTarget(new WeakReference<>(mView));
        Glide.with(App.self()).load(R.drawable.imageview_pin).into(pin);
        textView.setOnClickListener(view -> mListener.get().onItemClicked(mPosition, mMemo, new WeakReference<>(SelectableViewHolder.this).get()));
        textView.setOnLongClickListener(view -> mListener.get().onItemLongClicked(mPosition, mMemo, new WeakReference<>(SelectableViewHolder.this).get()));
        textView.setTypeface(App.FONT);
        textView.setTextSize(App.FONT_SIZE);
    }

    private SimpleTarget<Drawable> getTarget(WeakReference<View> view) {
        return new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.get().setBackground(resource);
            }
        };
    }

    @Override
    public void setSelectedState() {
        Glide.with(App.self()).load(mBackgroundSelectedId).into(mBackgroundTarget);
        pin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeselectedState() {
        Glide.with(App.self()).load(mBackgroundId).into(mBackgroundTarget);
        pin.setVisibility(View.VISIBLE);
    }

    private void setDeselectedAndExpandedState() {
        Glide.with(App.self()).load(mBackgroundExpandedId).into(mBackgroundTarget);
        pin.setVisibility(View.INVISIBLE);
    }

    void bind(Memo memo, int position, boolean isExpanded, boolean isSelected, int memoSize, int margins) {
        mMemo = memo;
        mPosition = position;
        setUpViewSizesAndMargins(memoSize, margins, isExpanded);
        setUpBackgroundColors(memo.getColor());
        setUpTextField(memo.getMemoText(), isExpanded, isSelected);
    }

    private void setUpViewSizesAndMargins(int memoSize, int margins, boolean isExpanded) {
        WeakReference<ConstraintLayout.LayoutParams> layoutParams = isExpanded ?
                new WeakReference<>(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)) :
                new WeakReference<>(new ConstraintLayout.LayoutParams(memoSize, memoSize));
        layoutParams.get().setMargins(margins, margins, margins, margins);
        mView.setLayoutParams(layoutParams.get());
    }

    private void setUpBackgroundColors(String memoColor) {
        ColorSpinnerAdapter.Colors color = ColorSpinnerAdapter.Colors.valueOf(memoColor);
        mBackgroundId = color.getMemoBackgroundId();
        mBackgroundSelectedId = color.getMemoBackgroundSelectedId();
        mBackgroundExpandedId = color.getMemoBackgroundExpandedId();
    }

    /*
    Using https://github.com/Saketme/Better-Link-Movement-Method for linking urls, email,
    phone and etc instead of built-in LinkMovementMethod due to bug described in link above
    */
    private void setUpTextField(String memoText, boolean isExpanded, boolean isSelected) {
        textView.setText(memoText);
        if (Patterns.WEB_URL.matcher(memoText).find()
                || Patterns.EMAIL_ADDRESS.matcher(memoText).find()
                || Patterns.PHONE.matcher(memoText).find()) {
            textView.setMovementMethod(BetterLinkMovementMethod.newInstance());
            Linkify.addLinks(textView, Linkify.ALL);
        }
        if (isExpanded) {
            setDeselectedAndExpandedState();
            textView.setMaxLines(Integer.MAX_VALUE);
            scrollView.setEnabled(true);
        } else {
            if (isSelected) {
                setSelectedState();
            } else {
                setDeselectedState();
            }
            setMaxLinesOnFontSize(App.FONT_SIZE);
            scrollView.setEnabled(false);
        }
    }

    private void setMaxLinesOnFontSize(int fontSize) {
//        int[] numOfLines = App.getNumOfLines();
//        int maxLines;
//        switch (fontSize) {
//            case 12:
//                maxLines = numOfLines[0];
//                break;
//            case 14:
//                maxLines = numOfLines[1];
//                break;
//            case 16:
//                maxLines = numOfLines[2];
//                break;
//            case 18:
//            case 20:
//                maxLines = numOfLines[3];
//                break;
//            default:
//                maxLines = numOfLines[4];
//                break;
//        }
//        textView.setMaxLines(maxLines);
    }

    interface OnItemSelectedListener {
        void onItemClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder);

        boolean onItemLongClicked(int mPosition, Memo mMemo, SelectableViewHolder viewHolder);

        boolean isMultiChoiceEnabled();

        void updateSelectedList(int pos, Memo memo, SelectableViewHolder viewHolder);

    }

}

