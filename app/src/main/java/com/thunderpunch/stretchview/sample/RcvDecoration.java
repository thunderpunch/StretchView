package com.thunderpunch.stretchview.sample;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Description:
 */

public class RcvDecoration extends RecyclerView.ItemDecoration {

    private final int verticalItemSpacingInPx;
    private final int horizontalItemSpacingInPx;
    private int orientation = LinearLayoutCompat.VERTICAL;

    public RcvDecoration(int verticalItemSpacingInPx, int horizontalItemSpacingInPx) {
        this.verticalItemSpacingInPx = verticalItemSpacingInPx;
        this.horizontalItemSpacingInPx = horizontalItemSpacingInPx;
    }

    public RcvDecoration(int verticalItemSpacingInPx, int horizontalItemSpacingInPx, int orientation) {
        this(verticalItemSpacingInPx, horizontalItemSpacingInPx);
        this.orientation = orientation;
    }

    /**
     * set border
     *
     * @param outRect outRect
     * @param view    view
     * @param parent  parent
     * @param state   state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        int itemPosition = layoutParams.getViewLayoutPosition();
        int childCount = parent.getAdapter().getItemCount();

        int left = getItemLeftSpace(itemPosition);
        int right = getItemRightSpace(itemPosition, childCount);

        int top = getItemTopSpacing(itemPosition);
        int bottom = getItemBottomSpacing(itemPosition, childCount);
        outRect.set(left, top, right, bottom);
    }


    /**
     * get the item_vertical bottom spacing
     *
     * @param itemPosition itemPosition
     * @param childCount   childCount
     * @return int
     */
    private int getItemBottomSpacing(int itemPosition, int childCount) {
        if (isLastItem(itemPosition, childCount) && orientation == LinearLayoutCompat.VERTICAL) {
            return this.verticalItemSpacingInPx;
        }
        return this.verticalItemSpacingInPx >> 1;
    }

    /**
     * get the item_vertical top spacing
     *
     * @param itemPosition itemPosition
     * @return int
     */
    private int getItemTopSpacing(int itemPosition) {
        if (isFirstItem(itemPosition) && orientation == LinearLayoutCompat.VERTICAL) {
            return verticalItemSpacingInPx;
        }
        return verticalItemSpacingInPx >> 1;
    }

    private int getItemLeftSpace(int itemPosition) {
        if (isFirstItem(itemPosition) && orientation == LinearLayoutCompat.HORIZONTAL) {
            return horizontalItemSpacingInPx;
        }
        return horizontalItemSpacingInPx >> 1;
    }

    private int getItemRightSpace(int itemPosition, int childCount) {
        if (isLastItem(itemPosition, childCount) && orientation == LinearLayoutCompat.HORIZONTAL) {
            return horizontalItemSpacingInPx;
        }
        return horizontalItemSpacingInPx >> 1;
    }

    /**
     * is the first item_vertical
     *
     * @param itemPosition itemPosition
     * @return boolean
     */
    private boolean isFirstItem(int itemPosition) {
        return itemPosition == 0;
    }


    /**
     * is the last item_vertical
     *
     * @param itemPosition itemPosition
     * @param childCount   childCount
     * @return boolean
     */
    private boolean isLastItem(int itemPosition, int childCount) {
        return itemPosition == childCount - 1;
    }
}