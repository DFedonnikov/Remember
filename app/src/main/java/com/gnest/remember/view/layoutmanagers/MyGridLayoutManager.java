package com.gnest.remember.view.layoutmanagers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.gnest.remember.App;

import rx.subjects.BehaviorSubject;

public class MyGridLayoutManager extends GridLayoutManager {

    public static final String LM_SCROLL_ORIENTATION_KEY = "LayoutManager orientation key";
    public static final String POSITION_KEY = "Position key";
    private static final long TRANSITION_DURATION_MS = 75;
    private static final float SCALE_THRESHOLD_PERCENT = 1f;

    private int mScreenWidth;
    private ExpandListener mExpandListener;

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        DisplayMetrics metrics = App.self().getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
    }

    private int getLargestSquareViewPosition() {
        int childCount = getChildCount();
        int maxSquare = 0;
        View maxSquareView = null;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            left = left > 0 ? left : 0;
            right = right <= mScreenWidth ? right : mScreenWidth;
            Rect viewRect = new Rect(left, top, right, bottom);

            int square = Math.abs(viewRect.height() * viewRect.width());
            if (square >= maxSquare) {
                maxSquare = square;
                maxSquareView = view;
            }
        }

        return maxSquareView != null ? getPosition(maxSquareView) : 0;
    }

    public void openItem(int pos) {
        if (getOrientation() == VERTICAL) {
            View viewToOpen = null;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                int position = getPosition(view);
                if (position == pos) {
                    viewToOpen = view;
                }
            }
            if (viewToOpen != null) {
                openView(viewToOpen, pos);
            }
        }

    }

    private void openView(final View viewToOpen, int positionToScroll) {
        int startTop = getDecoratedTop(viewToOpen);
        int startBottom = getDecoratedBottom(viewToOpen);
        int finishTop = 0;
        int finishBottom = getHeight();

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(TRANSITION_DURATION_MS);
        animator.addUpdateListener(animation -> {
            float animationProgress = (float) animation.getAnimatedValue();
            int top = (int) (startTop + animationProgress * (finishTop - startTop));
            int bottom = (int) (startBottom + animationProgress * (finishBottom - startBottom));
            layoutDecoratedWithMargins(viewToOpen, 0, top, getWidth(), bottom);
            updateViewScale();
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //No implementation needed
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setOrientation(HORIZONTAL);
                mExpandListener.expandItems();
                setSpanCount(1);
                scrollToPositionWithOffset(positionToScroll, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //No implementation needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //No implementation needed
            }
        });
        animator.start();
    }

    private void updateViewScale() {
        int childCount = getChildCount();
        int height = getHeight();
        int thresholdPx = (int) (height * SCALE_THRESHOLD_PERCENT);

        for (int i = 0; i < childCount; i++) {
            float scale = 1f;
            View view = getChildAt(i);
            int viewTop = getDecoratedTop(view);
            if (viewTop >= thresholdPx) {
                int delta = viewTop - thresholdPx;
                scale = (height - delta) / (float) height;
                scale = Math.max(scale, 0);
            }

            view.setPivotX(view.getHeight() / 2);
            view.setPivotY(view.getHeight() / -2);
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    public int getLastPosition() {
        return getLargestSquareViewPosition();
    }

    public void setExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
    }

    public interface ExpandListener {
        void expandItems();
    }
}
