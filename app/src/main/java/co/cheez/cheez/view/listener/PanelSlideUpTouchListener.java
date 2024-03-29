package co.cheez.cheez.view.listener;

import android.view.MotionEvent;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import co.cheez.cheez.util.DimensionUtil;

/**
 * Created by jiho on 5/21/15.
 */
public class PanelSlideUpTouchListener implements View.OnTouchListener {
    private float startX;
    private float startY;
    private int mSwipeOffset;

    private SlidingUpPanelLayout mPanelLayout;

    public PanelSlideUpTouchListener(SlidingUpPanelLayout slidingUpPanelLayout) {
        mPanelLayout = slidingUpPanelLayout;
        mSwipeOffset = (int) DimensionUtil.dpToPx(10);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                if (Math.abs(dy) > Math.abs(dx)
                        && -dy > mSwipeOffset) {
                    onSwipeUp();
                }
                break;
        }
        return true;
    }

    protected void onSwipeUp() {
        mPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}
