package com.thunderpunch.stretchview.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

/**
 * Created by thunderpunch on 2017/3/20
 * Description:
 */

public class DefaultDrawHelper extends StretchView.StretchDrawHelper {
    private Matrix mMatrix;
    private float mRatio = 0.05f;
    private float[] dst = new float[8];
    private RectF rect;

    public DefaultDrawHelper(StretchView v) {
        super(v);
        mMatrix = new Matrix();
        rect = new RectF();
    }

    @Override
    public boolean supportDirection(@StretchView.DirectionOption int direction) {
        return direction == StretchView.BOTTOM || direction == StretchView.LEFT ||
                direction == StretchView.RIGHT;
    }

    @Override
    public Matrix draw(Canvas canvas, int translation) {
        mMatrix.reset();
        final int contentSpace = v.getContentSpace();
        final int width = v.getWidth();
        final int height = v.getHeight();
        final float[] contentSrc = v.getContentSrc();
        if (v.getDirection() == StretchView.BOTTOM || v.getDirection() == StretchView.RIGHT) {
            translation = -translation;
        }
        if (translation <= 0) return mMatrix;

        switch (v.getDirection()) {
            case StretchView.LEFT:
                if (translation <= contentSpace) {
                    int directionOffset = (int) (mRatio * height * (contentSpace - translation) / contentSpace);
                    dst[0] = width - translation;//左上x
                    dst[1] = directionOffset;//左上y
                    dst[2] = width;//右上x
                    dst[3] = 0;//右上y
                    dst[4] = width;//右下x
                    dst[5] = height;//右下y
                    dst[6] = width - translation;//左下x
                    dst[7] = height - directionOffset;//左下y
                    mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
                    rect.set(width - translation, 0, width, height);
                } else {
                    translation = Math.min(translation, width);
                    dst[0] = width - translation;
                    dst[1] = 0;
                    dst[2] = width;
                    dst[3] = 0;
                    dst[4] = width;
                    dst[5] = height;
                    dst[6] = width - translation;
                    dst[7] = height;
                    mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
                    rect.set(dst[0], dst[1], dst[4], dst[5]);
                }
                break;
            case StretchView.RIGHT:
                if (translation <= contentSpace) {
                    int directionOffset = (int) (mRatio * height * (contentSpace - translation) / contentSpace);
                    dst[0] = 0;
                    dst[1] = 0;
                    dst[2] = translation;
                    dst[3] = directionOffset;
                    dst[4] = translation;
                    dst[5] = height - directionOffset;
                    dst[6] = 0;
                    dst[7] = height;
                    mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
                    rect.set(0, 0, translation, height);
                } else {
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
                    rect.set(dst[0], dst[1], dst[4], dst[5]);
                }
                break;
            case StretchView.BOTTOM:
                if (translation <= contentSpace) {
                    int directionOffset = (int) (mRatio * width * (contentSpace - translation) / contentSpace);
                    dst[0] = 0;
                    dst[1] = 0;
                    dst[2] = width;
                    dst[3] = 0;
                    dst[4] = width - directionOffset;
                    dst[5] = translation;
                    dst[6] = directionOffset;
                    dst[7] = translation;
                    mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
                    rect.set(0, 0, width, translation);
                } else {
                    translation = Math.min(translation, height);
                    dst[0] = 0;
                    dst[1] = 0;
                    dst[2] = width;
                    dst[3] = 0;
                    dst[4] = width;
                    dst[5] = translation;
                    dst[6] = 0;
                    dst[7] = translation;
                    mMatrix.setPolyToPoly(contentSrc, 0, dst, 0, 4);
                    rect.set(dst[0], dst[1], dst[4], dst[5]);
                }
                break;
        }
        return mMatrix;
    }

    @Override
    public void onDrawComplete(Canvas canvas, int translation) {
        final int contentSpaceH = v.getContentSpace();
        if (v.getDirection() == StretchView.BOTTOM || v.getDirection() == StretchView.RIGHT) {
            translation = -translation;
        }
        if (translation <= 0) return;
        if (translation <= contentSpaceH) {
            canvas.clipRect(rect);
            final int maxAlpha = (int) (255 * 0.7);
            canvas.drawColor((int) (maxAlpha * (1 - translation * 1.0f / contentSpaceH)) << 24);
        }
    }
}
