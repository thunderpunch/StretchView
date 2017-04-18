package com.thunderpunch.stretchview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.thunderpunch.stretchview.R;


/**
 * Created by thunderpunch on 2017/3/8
 * Description:
 */
@CoordinatorLayout.DefaultBehavior(StretchBehavior.class)
public class StretchView extends FrameLayout {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int BOTTOM = 3;
    protected float mStretchFactor;
    protected int mStretchSize;
    private int mWidth, mHeight;
    private int mTranslation;
    private float[] mContentSrc;
    private int mDirection;
    private StretchDrawHelper mDrawHelper;
    private OnStretchListener mListener;

    @IntDef({LEFT, RIGHT, BOTTOM})
    public @interface DirectionOption {
    }

    public StretchView(Context context) {
        this(context, null);
    }

    public StretchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StretchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StretchView);
        mStretchFactor = a.getFloat(R.styleable.StretchView_stretchFactor, 0.33f);
        mDirection = a.getInt(R.styleable.StretchView_direction, BOTTOM);
        a.recycle();
    }

    public int getContentSpace() {
        switch (mDirection) {
            case BOTTOM:
                return getHeight() - mStretchSize;
            case LEFT:
            case RIGHT:
                return getWidth() - mStretchSize;
        }
        return 0;
    }

    public void setDrawHelper(StretchDrawHelper drawHelper) {
        this.mDrawHelper = drawHelper;
    }

    public float[] getContentSrc() {
        return mContentSrc;
    }

    @DirectionOption
    public int getDirection() {
        return mDirection;
    }

    public int getStretchSize() {
        return mStretchSize;
    }

    /**
     * 在原本尺寸基础上加上可拉伸部分的长度作为总长度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        switch (mDirection) {
            case LEFT:
                if (mWidth != width) {
                    mStretchSize = (int) (width * mStretchFactor);
                    mWidth = width + mStretchSize;
                    mHeight = height;
                    mContentSrc = new float[]{mStretchSize, 0,
                            mWidth, 0,
                            mWidth, mHeight,
                            mStretchSize, mHeight
                    };
                    setMeasuredDimension(mWidth, mHeight);
                }
                break;
            case RIGHT:
                if (mWidth != width) {
                    mStretchSize = (int) (width * mStretchFactor);
                    mWidth = width + mStretchSize;
                    mHeight = height;
                    mContentSrc = new float[]{0, 0,
                            width, 0,
                            width, mHeight,
                            0, mHeight
                    };
                    setMeasuredDimension(mWidth, mHeight);
                }
                break;
            case BOTTOM:
                if (mHeight != height) {
                    mStretchSize = (int) (height * mStretchFactor);
                    mWidth = width;
                    mHeight = height + mStretchSize;
                    mContentSrc = new float[]{0, 0,
                            mWidth, 0,
                            mWidth, height,
                            0, height
                    };
                    setMeasuredDimension(mWidth, mHeight);
                }
                break;
        }
    }

    /**
     * 子布局以居中的形式放置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentLeft, parentRight, parentTop, parentBottom;
        switch (mDirection) {
            case LEFT:
                parentLeft = getPaddingLeft() + mStretchSize;
                parentRight = r - l - getPaddingRight();
                parentTop = getPaddingTop();
                parentBottom = b - t - getPaddingBottom();
                break;
            case RIGHT:
                parentLeft = getPaddingLeft();
                parentRight = r - l - getPaddingRight() - mStretchSize;
                parentTop = getPaddingTop();
                parentBottom = b - t - getPaddingBottom();
                break;
            case BOTTOM:
            default:
                parentLeft = getPaddingLeft();
                parentRight = r - l - getPaddingRight();
                parentTop = getPaddingTop();
                parentBottom = b - t - getPaddingBottom() - mStretchSize;
                break;
        }

        final View child = getChildAt(0);
        if (child == null || child.getVisibility() == GONE) return;
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();

        int childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                lp.leftMargin - lp.rightMargin;
        int childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                lp.topMargin - lp.bottomMargin;

        child.layout(childLeft, childTop, childLeft + width, childTop + height);
    }


    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("StretchView can host only one direct child");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("StretchView can host only one direct child");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("StretchView can host only one direct child");
        }
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("StretchView can host only one direct child");
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("StretchView can host only one direct child");
        }
        super.addView(child, width, height);
    }

    /**
     * @param translation 当direction等于left或right时，相当于translationX
     *                    当direction等于bottom时，相当于translationY
     */
    public void performTranslation(int translation) {
        mTranslation = translation;
        if (mListener != null) {
            mListener.onTranslation(mTranslation);
        }
        postInvalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View child = getChildAt(0);
        if (child == null || child.getVisibility() == GONE) return;
        if (mDrawHelper == null) {
            mDrawHelper = new DefaultDrawHelper(this);
        }
        if (mDrawHelper.supportDirection(mDirection)) {
            canvas.save();
            canvas.concat(mDrawHelper.draw(canvas, mTranslation));
            drawChild(canvas, child, getDrawingTime());
            canvas.restore();
            mDrawHelper.onDrawComplete(canvas, mTranslation);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    public void setOnStretchListener(OnStretchListener listener) {
        mListener = listener;
    }

    public interface OnStretchListener {
        void onTranslation(int trans);
    }

    public static abstract class StretchDrawHelper {

        protected StretchView v;

        public StretchDrawHelper(StretchView v) {
            this.v = v;
        }

        /**
         * @return 是否支持布局方向 {@link StretchView.DirectionOption}
         */
        public abstract boolean supportDirection(@DirectionOption int direction);

        /**
         * 绘制childview前的回调
         *
         * @return 绘制childview用到的变换矩阵
         */
        public abstract Matrix draw(Canvas canvas, int translation);

        /**
         * chidlview绘制完成后的回调
         */
        public void onDrawComplete(Canvas canvas, int translation) {

        }
    }
}
