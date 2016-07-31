package com.jeevitharoyapathi.assignment_2.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.PathInterpolator;

/**
 * Created by jeevitha.royapathi on 7/30/16.
 */
public final class animationUtils {
    public static void animateViewColor(View v, int startColor, int endColor) {
        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor",
                new ArgbEvaluator(), startColor, endColor);
        animator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
        animator.setDuration(1000);
        animator.start();
    }
}
