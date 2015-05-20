package co.cheez.cheez.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pkmmte.view.CircularImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.ContentViewActivity;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DimensionUtil;
import co.cheez.cheez.util.ViewAnimateUtil;

/**
 * Created by jiho on 5/13/15.
 */
public class ContentViewFragment extends BaseFragment
        implements SlidingUpPanelLayout.PanelSlideListener {
    public static final String KEY_POSITION = "position";
    private static final int TOOLBAR_HEIGHT = 48;
    private boolean mSourcePageLoaded = false;
    private Post mPost;
    private boolean mLinkClicked = false;

    @DeclareView(id = R.id.sliding_layout)
    private SlidingUpPanelLayout mSlidingLayout;

    @DeclareView(id = R.id.iv_content_image)
    private ImageView mContentImageView;

    @DeclareView(id = R.id.wv_content)
    private WebView mContentWebView;

    @DeclareView(id = R.id.tv_title)
    private TextView mTitleLabel;

    @DeclareView(id = R.id.tv_subtitle)
    private TextView mSubtitleLabel;

    @DeclareView(id = R.id.iv_user_profile)
    private CircularImageView mUserProfileImageView;

    @DeclareView(id = R.id.rl_umano_panel_handle)
    private View mDragView;

    @DeclareView(id = R.id.toolbar_default)
    private View mToolbar;

    private float mTitleTextSize;
    private float mSubtitleTextSize;
    private float mSmallTitleTextSize;
    private float mSmallSubtitleTextSize;
    private int mDragViewPadding;
    private int mSmallDragViewPadding;
    private int mDragViewHeight;
    private int mSmallDragViewHeight;



    public static BaseFragment newInstance(int position) {
        BaseFragment fragment = new ContentViewFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = getContentView(getActivity(), getLayoutResourceId());

        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        mPost = PostDataManager.getInstance().getPostAtPosition(position);
        ImageLoader.getInstance().displayImage(mPost.getImageUrl(), mContentImageView);
        mTitleLabel.setText(mPost.getTitle());
        mSubtitleLabel.setText(mPost.getSubtitle());

        mSlidingLayout.setDragView(mDragView);
        mSlidingLayout.setPanelSlideListener(this);

        String userProfileImageUrl = mPost.getUser().getImageUrl();
        if (userProfileImageUrl != null) {
            ImageLoader.getInstance().displayImage(userProfileImageUrl, mUserProfileImageView);
        }

        calculateViewSizes();
        mDragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!mSourcePageLoaded) {
                        mContentWebView.setWebViewClient(new WebViewClient());
                        mContentWebView.loadUrl(mPost.getSourceUrl());
                        mSourcePageLoaded = true;
                    }
                }
                return false;
            }
        });

        return contentView;
    }

    private void calculateViewSizes() {
        mTitleTextSize = mTitleLabel.getTextSize();
        mSubtitleTextSize = mSubtitleLabel.getTextSize();
        mSmallTitleTextSize = mTitleTextSize / 2;
        mSmallSubtitleTextSize = mSubtitleTextSize / 2;
        mDragViewPadding = mDragView.getPaddingTop();
        mSmallDragViewPadding = mDragViewPadding / 2;
        mDragViewHeight = mDragView.getLayoutParams().height;
        mSmallDragViewHeight = (int)DimensionUtil.dpToPx(TOOLBAR_HEIGHT);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_content_view;
    }

    @Override
    public void onPanelSlide(View view, float v) {
        setAnimationState(v);
    }

    private void setAnimationState(float v) {
        mTitleLabel.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                mTitleTextSize - ((mTitleTextSize - mSmallTitleTextSize) * v));
        mSubtitleLabel.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                mSubtitleTextSize - ((mSubtitleTextSize - mSmallSubtitleTextSize) * v));
        int newPadding = (int) (mDragViewPadding - ((mDragViewPadding - mSmallDragViewPadding) * v));
        mDragView.setPadding(
                newPadding,
                newPadding,
                newPadding,
                newPadding
        );

        ViewGroup.LayoutParams dragViewLayoutParams = mDragView.getLayoutParams();
        dragViewLayoutParams.height
                = (int) (mDragViewHeight - ((mDragViewHeight - mSmallDragViewHeight) * v));
        mDragView.setLayoutParams(dragViewLayoutParams);
    }

    @Override
    public void onPanelCollapsed(View view) {
        mContentWebView.pauseTimers();
        mContentWebView.onPause();
        setDragViewState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        ((ContentViewActivity)getActivity()).showToolbar();
    }

    @Override
    public void onPanelExpanded(View view) {
        mContentWebView.onResume();
        mContentWebView.resumeTimers();
        setDragViewState(SlidingUpPanelLayout.PanelState.EXPANDED);
        ((ContentViewActivity)getActivity()).hideToolbar(0);
        mLinkClicked = true;
    }

    private void setDragViewState(SlidingUpPanelLayout.PanelState state) {
        if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {


            ViewAnimateUtil.animateTextColor(
                    mTitleLabel,
                    mTitleLabel.getCurrentTextColor(),
                    Color.WHITE,
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
            ViewAnimateUtil.animateTextColor(
                    mSubtitleLabel,
                    mSubtitleLabel.getCurrentTextColor(),
                    Color.WHITE,
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
            ViewAnimateUtil.animateBackgroundColor(
                    mDragView,
                    ((ColorDrawable)mDragView.getBackground()).getColor(),
                    getResources().getColor(R.color.theme_primary),
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
        } else if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            ViewAnimateUtil.animateTextColor(
                    mTitleLabel,
                    mTitleLabel.getCurrentTextColor(),
                    getResources().getColor(R.color.text_title),
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
            ViewAnimateUtil.animateTextColor(
                    mSubtitleLabel,
                    mSubtitleLabel.getCurrentTextColor(),
                    getResources().getColor(R.color.text_subtitle),
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
            ViewAnimateUtil.animateBackgroundColor(
                    mDragView,
                    ((ColorDrawable)mDragView.getBackground()).getColor(),
                    Color.WHITE,
                    ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                    null
            );
        }
    }

    @Override
    public void onPanelAnchored(View view) {
    }

    @Override
    public void onPanelHidden(View view) {

    }

    @Override
    public void onPause() {
        try {
            JSONObject params = new JSONObject()
                    .put(Constants.Keys.POST_ID, mPost.getId())
                    .put(Constants.Keys.LINK_CLICKED, mLinkClicked);
            Request request = new AuthorizedRequest(
                    Request.Method.POST,
                    Constants.URLs.READ_POST,
                    params,
                    new DefaultListener(),
                    new DefaultErrorListener()
            );
            App.addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mContentWebView.setWebViewClient(new WebViewClient());
            mContentWebView.loadUrl(mPost.getSourceUrl());
            setAnimationState(1f);
            setDragViewState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    public boolean isSlideUpPanelShown() {
        return mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
    }

    public void showSlideUpPanel() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void hideSlideUpPanel() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }



    @Override
    public boolean onBackPressed() {
        if (mSlidingLayout != null
                && mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        } else {
            return false;
        }
    }
}
