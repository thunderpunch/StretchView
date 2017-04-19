package com.thunderpunch.stretchview.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.thunderpunch.stretchview.view.StretchView;

/**
 * Created by thunderpunch on 2017/3/20
 * Description:
 */

public class TranslationBehavior extends CoordinatorLayout.Behavior<View> {
    public TranslationBehavior() {
    }

    public TranslationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof StretchView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        StretchView v = (StretchView) dependency;
        switch (v.getDirection()) {
            case StretchView.BOTTOM:
                child.setTranslationY(Math.min(dependency.getY() - parent.getHeight(), 0));
                return true;
            case StretchView.LEFT:
                child.setTranslationX(Math.max(dependency.getX(), 0));
                return true;
            case StretchView.RIGHT:
                child.setTranslationX(Math.min(dependency.getX() - parent.getWidth(), 0));
                return true;
        }
        return false;
    }
}
