package com.thunderpunch.stretchview.sample;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.thunderpunch.stretchview.view.StretchView;

/**
 * Created by thunderpunch on 2017/3/29
 * Description:
 */

public class ArcDrawHelper extends StretchView.StretchDrawHelper {
    static final int HIDDEN = 0;
    static final int SHOW = 1;
    private int mBgColor;
    private Paint mPaint;
    private Matrix mMatrix;
    private int mState;
    private float[] dst = new float[8];
    private int mStartDistance;
    private float mThreshold = 0.87f;//阈值，弧顶点到达阈值距离前，弧顶点与滞后点的距离会不断增大，到达后距离减少
    private float mDelaySideShowSpeed = 0.2f;//展开时，到达阈值前滞后点的移动距离相对于弧顶点移动距离的比例
    private float mDelaySideCloseSpeed = 0.7f;//收起时，到达阈值前滞后点的移动距离相对于弧顶点移动距离的比例
    private Path mPath;
    private int mFill = 10;//填充绘制遗漏区域

    /**
     * @param startDist 形变前正常展开的距离
     */
    public ArcDrawHelper(StretchView v, int bgColor, int startDist) {
        super(v);
        mBgColor = bgColor;
        mPaint = new Paint();
        mMatrix = new Matrix();
        mPath = new Path();
        mStartDistance = startDist;

    }

    @Override
    public boolean supportDirection(@StretchView.DirectionOption int direction) {
        return direction == StretchView.RIGHT;
    }

    @Override
    public Matrix draw(Canvas canvas, int translation) {
        mMatrix.reset();
        final int contentSpace = v.getContentSpace();
        final int width = v.getWidth();
        final int height = v.getHeight();
        final float[] contentSrc = v.getContentSrc();
        translation = -translation;
        if (translation <= 0) {
            mState = HIDDEN;
            return mMatrix;
        }

        if (translation >= contentSpace) {
            mState = SHOW;
            translation = Math.min(translation, width);
            dst[0] = 0;
            dst[1] = 0;
            dst[2] = translation;
            dst[3] = 0;
            dst[4] = translation;
            dst[5] = height;
            dst[6] = 0;
            dst[7] = height;
            mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
            mPaint.setColor(mBgColor);
            canvas.drawRect(dst[0], dst[1], dst[4] + mFill, dst[5], mPaint);
        } else {
            if (translation <= mStartDistance) {
                mPaint.setColor(mBgColor);
                canvas.drawRect(0, 0, translation + mFill, height, mPaint);
                return mMatrix;
            }
            final int thresholdDis = (int) (mThreshold * (contentSpace - mStartDistance));
            int contentEdge;
            switch (mState) {
                case HIDDEN:
                    final int trans = translation - mStartDistance;
                    if (trans < thresholdDis) {
                        contentEdge = (int) (translation - (trans * mDelaySideShowSpeed + mStartDistance));
                    } else {
                        int remainDis = contentSpace - mStartDistance - thresholdDis;
                        final float percent = (trans - thresholdDis) * 1.0f / remainDis;
                        contentEdge = (int) ((thresholdDis * (1 - mDelaySideShowSpeed) + remainDis) * (1 - percent) - (remainDis - (trans - thresholdDis)));
                    }
                    mPath.rewind();
                    mPath.moveTo(contentEdge, 0);
                    mPath.quadTo(-contentEdge, height >> 1, contentEdge, height);
                    mPath.lineTo(translation + mFill, height);
                    mPath.lineTo(translation + mFill, 0);
                    mPath.close();
                    mPaint.setColor(mBgColor);
                    canvas.drawPath(mPath, mPaint);
                    mMatrix.postTranslate(contentEdge, 0);
                    break;

                case SHOW:
                    final int closeThresholdDis = (int) (thresholdDis * mDelaySideCloseSpeed);
                    final int disFromStart = contentSpace - translation;
                    if (disFromStart < closeThresholdDis) {
                        contentEdge = (int) (translation - (contentSpace - disFromStart / mDelaySideCloseSpeed));
                    } else {
                        int totalRemain = contentSpace - mStartDistance - closeThresholdDis;
                        float percent = (disFromStart - closeThresholdDis) * 1.0f / totalRemain;
                        contentEdge = (int) (translation - (contentSpace - ((contentSpace - mStartDistance - thresholdDis) * percent + thresholdDis)));
                    }
                    mPath.rewind();
                    mPath.moveTo(0, 0);
                    mPath.quadTo(contentEdge << 1, height >> 1, 0, height);
                    mPath.lineTo(translation + mFill, height);
                    mPath.lineTo(translation + mFill, 0);
                    mPath.close();
                    mPaint.setColor(mBgColor);
                    canvas.drawPath(mPath, mPaint);
                    mMatrix.postTranslate(contentEdge, 0);
                    break;
            }
        }
        return mMatrix;
    }
}
