package co.cheez.cheez.automation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by jiho on 5/24/15.
 */
public class ScrollObservableWebView extends WebView {
    private OnScrollListener mOnScrollListener;
    private int mMeasuredContentHeight = 0;

    public ScrollObservableWebView(Context context) {
        super(context);
        initialize();
    }

    public ScrollObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ScrollObservableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(l, t, oldl, oldt);
        }
    }

    public int getMeasuredContentHeight() {
        return mMeasuredContentHeight;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public interface OnScrollListener {
        void onScroll(int l, int t, int oldl, int oldt);
    }

}
