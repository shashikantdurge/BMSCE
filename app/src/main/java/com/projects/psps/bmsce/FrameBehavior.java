package com.projects.psps.bmsce;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

/**
 * Created by ${SHASHIKANt} on 30-07-2017.
 */

public class FrameBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    //Required to instantiate as a default behavior
    public FrameBehavior() {
    }

    //Required to attach behavior via XML
    public FrameBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    //This is called to determine which views this behavior depends on
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   FrameLayout child,
                                   View dependency) {
        //We are watching changes in the AppBarLayout
        return dependency instanceof AppBarLayout;
    }

    //This is called for each change to a dependent view
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,
                                          FrameLayout child,
                                          View dependency) {
        int offset = -dependency.getTop();
        child.setTranslationY(offset);
        return true;
    }
}