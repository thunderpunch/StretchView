package com.thunderpunch.stretchview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.thunderpunch.stretchview.view.StretchView;

/**
 * Created by FEN ZENG on 2017/3/17
 * Description:
 */

public class StretchBehavior extends CoordinatorLayout.Behavior<StretchView> {
    private Interpolator mStretchInterpolator = new DecelerateInterpolator(3);
    private float mStretchStart = 0.2f;
    private float mStretchEnd = 0.02f;
    private boolean mSkipNestedPreScroll, mIsFling;
    private ValueAnimator mTransAnimator;
    private int mTranslation;

    StretchBehavior() {
    }

    public StretchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, final StretchView child, MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, StretchView child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            ViewCompat.setFitsSystemWindows(child, true);
        }
        parent.onLayoutChild(child, layoutDirection);
        //设置起始位置
        switch (child.getDirection()) {
            case StretchView.LEFT:
                ViewCompat.offsetLeftAndRight(child, -child.getWidth() + mTranslation);
                break;
            case StretchView.RIGHT:
                ViewCompat.offsetLeftAndRight(child, parent.getWidth() - child.getLeft() - parent.getPaddingRight() + mTranslation);
                break;
            case StretchView.BOTTOM:
                ViewCompat.offsetTopAndBottom(child, parent.getHeight() - child.getTop() - parent.getPaddingBottom() + mTranslation);
                break;
        }
        return true;
    }

    public void runAnimation(final StretchView v, int startTrans, int endTrans, int duration) {
        if (mTransAnimator == null) {
            mTransAnimator = new ValueAnimator();
            mTransAnimator.setInterpolator(new AccelerateInterpolator());
        }
        mTransAnimator.setIntValues(startTrans, endTrans);
        mTransAnimator.setDuration(duration);
        mTransAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateChild(v, (Integer) animation.getAnimatedValue() - mTranslation);
            }
        });
        mTransAnimator.start();
    }

    /**
     * @return 滑动视图是否到达边缘
     */
    private boolean determineEdge(View target, int direction) {
        switch (direction) {
            case StretchView.LEFT:
                return !ViewCompat.canScrollHorizontally(target, -1);
            case StretchView.RIGHT:
                return !ViewCompat.canScrollHorizontally(target, 1);
            case StretchView.BOTTOM:
                return !ViewCompat.canScrollVertically(target, 1);
            default:
                return false;
        }
    }

    private int convert2Distance(int translation, int direction) {
        switch (direction) {
            case StretchView.LEFT:
                return translation;
            case StretchView.BOTTOM:
            case StretchView.RIGHT:
                return -translation;
        }
        return translation;
    }

    /**
     * @param distance 相对于起始位置{@link #onLayoutChild(CoordinatorLayout, StretchView, int)}的距离
     * @return translation
     */
    private int convert2Translation(int distance, int direction) {
        switch (direction) {
            case StretchView.LEFT:
                return distance;
            case StretchView.BOTTOM:
            case StretchView.RIGHT:
                return -distance;
        }
        return distance;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, final StretchView child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        if (!mIsFling) {
            final int direction = child.getDirection();
            int curDist = convert2Distance(mTranslation, direction);
            int contentSpace = child.getContentSpace();
            int endDist, duration;
            if (curDist > 0) {
                if (curDist > contentSpace) {
                    //恢复正常尺寸
                    endDist = contentSpace;
                    duration = Math.round(Math.abs(150 * (endDist - curDist) / (child.getStretchSize())));
                } else {
                    //展开超过一半，那么完全展开，否则折叠
                    endDist = curDist >= contentSpace * 0.5 ? contentSpace : 0;
                    duration = Math.round(Math.abs(800 * (endDist - curDist)) / (contentSpace));
                }
                runAnimation(child, convert2Translation(curDist, direction),
                        convert2Translation(endDist, direction), duration);
            }
        }
        mSkipNestedPreScroll = false;
        mIsFling = false;
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, StretchView child, View directTargetChild, View target, int nestedScrollAxes) {
        final int direction = child.getDirection();
        boolean start = ((direction == StretchView.LEFT || direction == StretchView.RIGHT)
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0 &&
                coordinatorLayout.getWidth() - directTargetChild.getWidth() <= child.getWidth()) ||
                (direction == StretchView.BOTTOM && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 &&
                        coordinatorLayout.getHeight() - directTargetChild.getHeight() <= child.getHeight());
        if (start && mTransAnimator != null) {
            mTransAnimator.cancel();
        }
        return start;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, StretchView child, View target, int dx, int dy, int[] consumed) {
        final int direction = child.getDirection();
        if ((direction == StretchView.LEFT || direction == StretchView.RIGHT) && (!mSkipNestedPreScroll) && dx != 0) {
            consumed[0] = translation(child, target, dx);
        } else if (direction == StretchView.BOTTOM && (!mSkipNestedPreScroll) && dy != 0) {
            consumed[1] = translation(child, target, dy);
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, StretchView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        final int direction = child.getDirection();
        if (direction == StretchView.LEFT && dxUnconsumed < 0) {
            translation(child, target, dxUnconsumed);
            mSkipNestedPreScroll = true;
        } else if (direction == StretchView.BOTTOM && dyUnconsumed > 0) {
            translation(child, target, dyUnconsumed);
            mSkipNestedPreScroll = true;
        } else if (direction == StretchView.RIGHT && dxUnconsumed > 0) {
            translation(child, target, dxUnconsumed);
            mSkipNestedPreScroll = true;
        } else {
            mSkipNestedPreScroll = false;
        }
    }

    private int translation(StretchView child, View target, int delta) {
        final int direction = child.getDirection();
        final int conentSpace = child.getContentSpace();
        final int curDist = convert2Distance(mTranslation, direction);
        final int dDist = convert2Distance(delta, direction);
        int maxDist = 0;
        switch (direction) {
            case StretchView.LEFT:
            case StretchView.RIGHT:
                maxDist = child.getWidth();
                break;
            case StretchView.BOTTOM:
                maxDist = child.getHeight();
                break;
        }
        int consumed = 0;
        if (dDist < 0) {//展开
            if (curDist < conentSpace) {//在内容视图尺寸范围内
                final int targetDist = curDist - dDist;
                final int newDist = Math.min(targetDist, conentSpace);
                consumed = convert2Translation(targetDist != newDist ? curDist - newDist : dDist, direction);
                updateChild(child, convert2Translation(newDist - curDist, direction));
            } else {//超出内容视图尺寸
                if (determineEdge(target, direction)) {
                    float input = (curDist - conentSpace) * 1.0f / child.getStretchSize();
                    int actionDist = (int) (dDist * (mStretchStart + (mStretchEnd - mStretchStart) *
                            mStretchInterpolator.getInterpolation(input)));

                    int targetDist = curDist - actionDist;
                    int newDist = Math.min(maxDist, targetDist);
                    consumed = convert2Translation(targetDist != newDist ? curDist - newDist : dDist, direction);
                    updateChild(child, convert2Translation(newDist - curDist, direction));
                }
            }
        } else {//折叠
            if (curDist > 0) {
                final int targetDist = curDist - dDist;
                final int newDist = Math.max(0, targetDist);
                consumed = convert2Translation(targetDist != newDist ? curDist - newDist : dDist, direction);
                convert2Translation(dDist, direction);
                updateChild(child, convert2Translation(newDist - curDist, direction));
            }
        }
        return consumed;
    }

    private void updateChild(StretchView v, int offset) {
        mTranslation += offset;
        v.performTranslation(mTranslation);
        switch (v.getDirection()) {
            case StretchView.LEFT:
            case StretchView.RIGHT:
                v.offsetLeftAndRight(offset);
                break;
            case StretchView.BOTTOM:
                v.offsetTopAndBottom(offset);
                break;
        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, StretchView child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, final StretchView child, View target, float velocityX, float velocityY, boolean consumed) {
        final int direction = child.getDirection();
        int velocity = 0;
        switch (direction) {
            case StretchView.LEFT:
            case StretchView.RIGHT:
                velocity = convert2Distance((int) velocityX, direction);
                break;
            case StretchView.BOTTOM:
                velocity = convert2Distance((int) velocityY, direction);
                break;
        }
        if (velocity != 0) {
            return mIsFling = setIsShow(child, velocity < 0);
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    /**
     * @param show ture 展开 false 折叠
     * @return 是否执行了相应的动画
     */
    public boolean setIsShow(StretchView sv, boolean show) {
        final int direction = sv.getDirection();
        final int curDist = convert2Distance(mTranslation, direction);
        final int contentSpace = sv.getContentSpace();
        int endDist, duration;
        if (curDist <= contentSpace) {
            if (show) {
                endDist = contentSpace;
                duration = 800 * Math.abs(endDist - curDist) / contentSpace;
            } else {
                endDist = 0;
                duration = 800 * Math.abs(endDist - curDist) / contentSpace;
            }
            runAnimation(sv, convert2Translation(curDist, direction),
                    convert2Translation(endDist, direction), duration);
            return true;
        }
        return false;
    }
}
