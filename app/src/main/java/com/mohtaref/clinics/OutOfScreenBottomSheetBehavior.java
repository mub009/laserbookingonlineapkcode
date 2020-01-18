package com.mohtaref.clinics;
import android.content.Context;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;



/**
 * {@link BottomSheetBehavior} that shows automatically when the dependency goes out of the screen
 * and hides when it comes back in.
 */

public class OutOfScreenBottomSheetBehavior extends BottomSheetBehavior<FrameLayout> {

    private int statusBarHeight;

    public OutOfScreenBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return dependency.getId() == R.id.behavior_dependency;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        int[] dependencyLocation = new int[2];

        dependency.getLocationInWindow(dependencyLocation);
        Log.d("BEHAVIOR", "Location: " + dependencyLocation[1]);

        if (dependencyLocation[1] <= statusBarHeight) {
            if (getState() != STATE_EXPANDED) {
                setState(STATE_EXPANDED);
            }
        } else {
            setState(STATE_HIDDEN);
        }
        return false;
    }

}