package com.gnest.remember.view.layoutmanagers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import com.gnest.remember.App;

import java.util.ArrayList;

import rx.subjects.BehaviorSubject;

public class MyGridLayoutManager extends GridLayoutManager {

    private static final long TRANSITION_DURATION_MS = 300;
    private static final float SCALE_THRESHOLD_PERCENT = 1f;
    private final BehaviorSubject<Boolean> childrenLayoutCompleteSubject = BehaviorSubject.create();

    private SparseArray<View> mViewCache = new SparseArray<>();
    private int mMemoSize;

    private int mScreenWidth;
    private int mMagrings;
    private int mAncorPos;
    private int mCurrentOrientation;
    private int mLastPosition;
    private ExpandListener mExpandListener;

    public MyGridLayoutManager(Context context, int spanCount, int memoSize, int margins) {
        super(context, spanCount);
        DisplayMetrics metrics = App.self().getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        this.mMemoSize = memoSize;
        this.mMagrings = margins;
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        mCurrentOrientation = orientation;
        View anchorView = getAnchorView();
        if (anchorView != null) {
            mAncorPos = getPosition(anchorView);
        }
        requestLayout();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
        childrenLayoutCompleteSubject.onNext(true);
    }

    private void fill(RecyclerView.Recycler recycler) {
        View anchorView = getAnchorView();
        mViewCache.clear();

        //Помещаем вьюшки в кэш и...
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            mViewCache.put(pos, view);
        }

        //... и удалям из лэйаута
        for (int i = 0; i < mViewCache.size(); i++) {
            detachView(mViewCache.valueAt(i));
        }


        switch (mCurrentOrientation) {
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
        for (int i = 0; i < mViewCache.size(); i++) {
            recycler.recycleView(mViewCache.valueAt(i));
        }

        updateViewScale();
    }

    private void fillUp(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        int anchorTop = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorTop = getDecoratedTop(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        boolean fillUp = true;
        int pos = anchorPos - 1;

        int viewBottom = anchorTop;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(mMemoSize + 2 * mMagrings, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(mMemoSize + 2 * mMagrings, View.MeasureSpec.EXACTLY);

        while (fillUp && pos >= 0) {
            View view = mViewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view, 0);
                measureChildWithDecorations(view, widthSpec, heightSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                int column = getColumnOfPosition(pos);
                int left = decoratedMeasuredWidth * column;
                layoutDecoratedWithMargins(view, left, viewBottom - decoratedMeasuredHeight, left + decoratedMeasuredWidth, viewBottom);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view, 0);
                mViewCache.remove(pos);
            }

            if (isViewLeftmost(pos)) {
                viewBottom = getDecoratedTop(view);
                fillUp = (viewBottom > 0);
            }
            pos--;
        }
    }

    private void fillDown(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos;
        int anchorTop = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorTop = getDecoratedTop(anchorView);
        } else {
            anchorPos = mAncorPos;
        }

        int pos = anchorPos;
        boolean fillDown = true;
        int height = getHeight();
        int viewTop = anchorTop;
        int itemCount = getItemCount();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(mMemoSize + 2 * mMagrings, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(mMemoSize + 2 * mMagrings, View.MeasureSpec.EXACTLY);

        while (fillDown && pos < itemCount) {
            View view = mViewCache.get(pos);
            if (view == null) {
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithDecorations(view, widthSpec, heightSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                int column = getColumnOfPosition(pos);
                int left = decoratedMeasuredWidth * column;
                layoutDecoratedWithMargins(view, left, viewTop, left + decoratedMeasuredWidth, viewTop + decoratedMeasuredHeight);
            } else {
                attachView(view);
                mViewCache.remove(pos);

            }

            //If view is rightmost we should check if we need to layout next row of elements
            if (isViewRightmost(pos)) {
                viewTop = getDecoratedBottom(view);
                fillDown = viewTop <= height;
            }
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
            View view = mViewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view, 0);
                measureChildWithDecorations(view, widthSpec, heigthSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewRight - decoratedMeasuredWidth, 0, viewRight, decoratedMeasuredHeight);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view);
                mViewCache.remove(pos);
            }
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
            View view = mViewCache.get(pos); //проверяем кэш
            if (view == null) {
                //если вьюшки нет в кэше - просим у recycler новую, измеряем и лэйаутим её
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithDecorations(view, widthSpec, heigthSpec);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                layoutDecorated(view, viewLeft, 0, viewLeft + decoratedMeasuredWidth, decoratedMeasuredHeight);
            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.
                attachView(view);
                mViewCache.remove(pos);
            }
            viewLeft = getDecoratedRight(view);
            fillRight = viewLeft <= width;
            pos++;
        }
        mLastPosition = getLargestSquareViewPosition();
    }

    private View getAnchorView() {
        int childCount = getChildCount();
        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int maxSquare = 0;
        View anchorView = null;
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

    private void measureChildWithDecorations(View child, int widthSpec, int heightSpec) {
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        child.measure(widthSpec, heightSpec);
    }

    private boolean isViewLeftmost(int viewPosition) {
        return (viewPosition) % getSpanCount() == 0;
    }

    private boolean isViewRightmost(int viewPosition) {
        return (viewPosition + 1) % getSpanCount() == 0;
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
        animator.addUpdateListener(animation -> {
            float animationProgress = (float) animation.getAnimatedValue();
            for (ViewAnimationInfo animationInfo : animationInfos) {
                int top = (int) (animationInfo.startTop + animationProgress * (animationInfo.finishTop - animationInfo.startTop));
                int bottom = (int) (animationInfo.startBottom + animationProgress * (animationInfo.finishBottom - animationInfo.startBottom));
                layoutDecorated(animationInfo.view, 0, top, getWidth(), bottom);
            }
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

    private static class ViewAnimationInfo {
        int startTop;
        int startBottom;
        int finishTop;
        int finishBottom;
        View view;
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
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int delta = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-delta);
        fill(recycler);
        return delta;
    }

    private int scrollVerticallyInternal(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
            return 0;
        }

        final View topView = getChildAt(0);
        final View bottomView = getChildAt(childCount - 1);

        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (viewSpan <= getHeight()) {
            return 0;
        }

        int delta = 0;
        if (dy < 0) {
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) {
                delta = dy;
            } else {
                int viewTop = getDecoratedTop(firstView) - mMagrings;
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0) {
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) {
                delta = dy;
            } else {
                int viewBottom = getDecoratedBottom(lastView) + mMagrings;
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
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

    public void setAncorPos(int ancorPos) {
        this.mAncorPos = ancorPos;
    }

    public int getLastPosition() {
        return mLastPosition;
    }

    // Return the column index of this position
    private int getColumnOfPosition(int position) {
        return position % getSpanCount();
    }

    public void setExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
    }

    public BehaviorSubject<Boolean> getChildrenLayoutCompleteSubject() {
        return childrenLayoutCompleteSubject;
    }

    public interface ExpandListener {
        void expandItems();
    }
}
