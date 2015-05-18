package co.cheez.cheez.util;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.concurrent.Callable;

/**
 * Created by jiho on 5/18/15.
 */
public class ViewAnimateUtil {
    public static final long ANIMATION_DURATION_DEFAULT = 300;

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
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        onAnimationFinished.call();
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

        }
        animator.start();
    }


}
