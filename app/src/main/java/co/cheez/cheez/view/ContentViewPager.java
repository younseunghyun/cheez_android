package co.cheez.cheez.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by jiho on 5/25/15.
 */
public class ContentViewPager extends ViewPager {
    private SwipeEnabledChecker mSwipeEnabledChecker;


    public ContentViewPager(Context context) {
        super(context);
    }

    public ContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSwipeEnabledChecker != null
                && !mSwipeEnabledChecker.isSwipeEnabled()) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mSwipeEnabledChecker != null
                && !mSwipeEnabledChecker.isSwipeEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setSwipeEnabledChecker(SwipeEnabledChecker checker) {
        mSwipeEnabledChecker = checker;
    }

    public interface SwipeEnabledChecker {
        boolean isSwipeEnabled();
    }
}
