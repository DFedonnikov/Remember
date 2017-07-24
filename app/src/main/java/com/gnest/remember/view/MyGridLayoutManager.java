package com.gnest.remember.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.gnest.remember.R;

import java.util.ArrayList;

/**
 * Created by DFedonnikov on 23.07.2017.
 */

public class MyGridLayoutManager extends GridLayoutManager {

    private static final long TRANSITION_DURATION_MS = 300;
    private static final float SCALE_THRESHOLD_PERCENT = 0.66f;
    private static final int TV_PADDING_TOP_EXTENDED = 110;
    private static final int TV_PADDING_TOP_UNEXTENDED = 30;

    private SparseArray<View> viewCache = new SparseArray<>();
    private int mAncorPos;
    private int currentOrientation;

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        currentOrientation = orientation;
        View anchorView = getAnchorView();
        mAncorPos = anchorView != null ? getPosition(anchorView) : 0;
        requestLayout();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {
        View anchorView = getAnchorView();
        viewCache.clear();

        //Помещаем вьюшки в кэш и...
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        //... и удалям из лэйаута
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }


        switch (currentOrientation) {
            case VERTICAL:
                mAncorPos = 0;
                fillUp(anchorView, recycler);
                fillDown(anchorView, recycler);
                break;
            case HORIZONTAL:
                fillLeft(anchorView, recycler);
                fillRight(anchorView, recycler);
                break;
        }


        //отправляем в корзину всё, что не потребовалось в этом цикле лэйаута
        //эти вьюшки или ушли за экран или не понадобились, потому что соответствующие элементы
        //удалились из адаптера
        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateViewScale();
    }

    private void fillUp(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        boolean fillUp = true;
        int pos = anchorPos - 1;
        int viewBottom;


        while (fillUp && pos >= 0) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view, 0);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heigthSpec);
//                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
//                layoutDecorated(view, 0, viewBottom - viewHeight, decoratedMeasuredWidth, viewBottom);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view);
                viewCache.remove(pos);
            }
            adjustTextViewPaddingTop(view);
            viewBottom = getDecoratedTop(view);
            fillUp = (viewBottom > 0);
            pos--;
        }
    }

    private void fillDown(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        int pos = anchorPos;
        boolean fillDown = true;
        int height = getHeight();
        int viewTop;
        int itemCount = getItemCount();

        while (fillDown && pos < itemCount) {
            View view = viewCache.get(pos);
            if (view == null) {
                view = recycler.getViewForPosition(pos);
                addView(view);
            } else {
                attachView(view);
                viewCache.remove(pos);
            }
            adjustTextViewPaddingTop(view);
            viewTop = getDecoratedBottom(view);
            fillDown = viewTop <= height;
            pos++;
        }
    }

    private void fillLeft(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        int anchorLeft = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        boolean fillLeft = true;
        int pos = anchorPos - 1;
        int viewRight = anchorLeft; //правая граница следующей вьюшки будет начитаться от левой границы предыдущей
        int width = getWidth();
        int height = getHeight();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        final int heigthSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        while (fillLeft && pos >= 0) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view, 0);
                measureChildWithDecorationsAndMargin(view, widthSpec, heigthSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewRight - decoratedMeasuredWidth, 0, viewRight, decoratedMeasuredHeight);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view);
                viewCache.remove(pos);
            }
            adjustTextViewPaddingTop(view);
            viewRight = getDecoratedLeft(view);
            fillLeft = (viewRight > 0);
            pos--;
        }
    }

    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        int anchorLeft = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        int pos = anchorPos;
        boolean fillRight = true;
        int viewLeft = anchorLeft; //левая граница следующей вьюшки будет начитаться от левой границы предыдущей
        int itemCount = getItemCount();
        int width = getWidth();
        int height = getHeight();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        final int heigthSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        while (fillRight && pos < itemCount) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithDecorationsAndMargin(view, widthSpec, heigthSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewLeft, 0, viewLeft + decoratedMeasuredWidth, decoratedMeasuredHeight);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view);
                viewCache.remove(pos);
            }
            adjustTextViewPaddingTop(view);
            viewLeft = getDecoratedRight(view);
            fillRight = viewLeft <= width;
            pos++;
        }

    }

    private void adjustTextViewPaddingTop(View view) {
        int paddingTopInPx = 0;
        float scale = view.getContext().getResources().getDisplayMetrics().density;
        if (currentOrientation == VERTICAL) {
            paddingTopInPx = (int) (TV_PADDING_TOP_UNEXTENDED * scale + 0.5f);
        } else {
            paddingTopInPx = (int) (TV_PADDING_TOP_EXTENDED * scale + 0.5f);
        }
        View mTextView = view.findViewById(R.id.memo_textView);
        mTextView.setPadding(mTextView.getPaddingLeft(), paddingTopInPx, mTextView.getPaddingRight(), mTextView.getPaddingBottom());
    }


    private View getAnchorView() {
        int childCount = getChildCount();
        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int maxSquare = 0;
        View anchorView = null;
//        HashMap<Integer, View> viewsOnScreen = new HashMap<>();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect) {
                int square = viewRect.width() * viewRect.height();
                if (square > maxSquare) {
                    anchorView = view;
                }
            }
        }
        return anchorView;
    }

    public void openItem(int pos) {
        if (currentOrientation == VERTICAL) {
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
                openView(viewToOpen);
            }
        }
    }

    private void openView(final View viewToOpen) {
        final ArrayList<ViewAnimationInfo> animationInfos = new ArrayList<>();
        int childCount = getChildCount();
        int animatedPos = getPosition(viewToOpen);
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            int posDelta = pos - animatedPos;
            final ViewAnimationInfo viewAnimationInfo = new ViewAnimationInfo();
            viewAnimationInfo.startTop = getDecoratedTop(view);
            viewAnimationInfo.startBottom = getDecoratedBottom(view);
            viewAnimationInfo.finishTop = getHeight() * posDelta;
            viewAnimationInfo.finishBottom = getHeight() * posDelta + getHeight();
            viewAnimationInfo.view = view;
            animationInfos.add(viewAnimationInfo);
        }

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(TRANSITION_DURATION_MS);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animationProgress = (float) animation.getAnimatedValue();
                for (ViewAnimationInfo animationInfo : animationInfos) {
                    int top = (int) (animationInfo.startTop + animationProgress * (animationInfo.finishTop - animationInfo.startTop));
                    int bottom = (int) (animationInfo.startBottom + animationProgress * (animationInfo.finishBottom - animationInfo.startBottom));
                    layoutDecorated(animationInfo.view, 0, top, getWidth(), bottom);
                }
                updateViewScale();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setOrientation(HORIZONTAL);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left, lp.rightMargin + decorRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top, lp.bottomMargin + decorRect.bottom);
        child.measure(widthSpec, heightSpec);
    }


    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }

        final int mode = View.MeasureSpec.getMode(spec);
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
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

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int delta = scrollHorizontallyInternal(dx);
        offsetChildrenHorizontal(-delta);
        fill(recycler);
        return delta;
    }

    private int scrollHorizontallyInternal(int dx) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
            return 0;
        }

        final View leftView = getChildAt(0);
        final View rightView = getChildAt(childCount - 1);

        //Случай, когда все вьюшки поместились на экране
        int viewSpan = getDecoratedRight(rightView) - getDecoratedLeft(leftView);
        if (viewSpan <= getWidth()) {
            return 0;
        }

        int delta = 0;
        //если контент уезжает влево
        if (dx < 0) {
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) { //если левая вюшка не самая первая в адаптере
                delta = dx;
            } else { //если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                int viewLeft = getDecoratedLeft(firstView);
                delta = Math.max(viewLeft, dx);
            }
        } else if (dx > 0) { //если контент уезжает вправо
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) { //если правая вюшка не самая последняя в адаптере
                delta = dx;
            } else { //если правая вьюшка самая последняя в адаптере и правее вьюшек больше быть не может
                int viewRight = getDecoratedRight(lastView);
                delta = Math.min(viewRight - getWidth(), dx);
            }
        }
        return delta;
    }

    private static class ViewAnimationInfo {
        int startTop;
        int startBottom;
        int finishTop;
        int finishBottom;
        View view;
    }
}
