package co.cheez.cheez.util;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Callable;

/**
 * Created by jiho on 5/18/15.
 */
public class ViewAnimateUtil {
    public static final long ANIMATION_DURATION_DEFAULT = 300;

    public static void animateBackgroundColor (
            final View targetView,
            int fromColor,
            int toColor,
            long duration,
            final Callable onAnimationFinished) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int textColor = (Integer) animation.getAnimatedValue();
                targetView.setBackgroundColor(textColor);
            }
        });
        if (onAnimationFinished != null) {
            animator = setAnimationEndCallback(animator, onAnimationFinished);
        }
        animator.start();
    }

    public static void animateTextColor(
            final TextView targetView,
            int fromColor,
            int toColor,
            long duration,
            final Callable onAnimationFinished) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int textColor = (Integer) animation.getAnimatedValue();
                targetView.setTextColor(textColor);
            }
        });
        if (onAnimationFinished != null) {
            animator = setAnimationEndCallback(animator, onAnimationFinished);
        }
        animator.start();
    }

    public static void animateAlpha(final View targetView,
                                    float fromAlpha,
                                    float toAlpha,
                                    long duration,
                                    final Callable onAnimationFinished) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromAlpha, toAlpha);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float)animation.getAnimatedValue();
                targetView.setAlpha(alpha);
            }
        });
        if (onAnimationFinished != null) {
            animator = setAnimationEndCallback(animator, onAnimationFinished);
        }
        animator.start();
    }

    public static ValueAnimator setAnimationEndCallback(ValueAnimator animator, final Callable callback) {
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }
}
