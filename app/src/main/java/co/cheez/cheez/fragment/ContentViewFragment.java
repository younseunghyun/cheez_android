package co.cheez.cheez.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.pkmmte.view.CircularImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.Callable;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.ProfileActivity;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ScrollObservableWebView;
import co.cheez.cheez.dialog.CommentDialog;
import co.cheez.cheez.event.PanelStateChangedEvent;
import co.cheez.cheez.event.PostReadEvent;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.model.ReadPostRel;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DimensionUtil;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.ViewAnimateUtil;
import co.cheez.cheez.view.listener.ContentMenuItemClickListener;
import co.cheez.cheez.view.listener.PanelSlideUpTouchListener;
import de.greenrobot.event.EventBus;

/**
 * Created by jiho on 5/13/15.
 */
public class ContentViewFragment extends BaseFragment
        implements SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener {
    public static final String KEY_POSITION = "position";

    private static final int TOOLBAR_HEIGHT_DP = 48;
    private boolean mSourcePageLoaded = false;
    private Post mPost;
    private boolean mLinkClicked = false;

    @DeclareView(id = R.id.sliding_layout)
    private SlidingUpPanelLayout mSlidingLayout;

    @DeclareView(id = R.id.iv_content_image)
    private ImageView mContentImageView;

    @DeclareView(id = R.id.wv_content)
    private ScrollObservableWebView mContentWebView;

    @DeclareView(id = R.id.tv_title)
    private TextView mTitleLabel;

    @DeclareView(id = R.id.tv_subtitle)
    private TextView mSubtitleLabel;

    @DeclareView(id = R.id.iv_user_profile, click = "this")
    private CircularImageView mUserProfileImageView;

    @DeclareView(id = R.id.rl_umano_panel_handle)
    private View mDragView;

    @DeclareView(id = R.id.toolbar_default)
    private View mToolbar;

    @DeclareView(id = R.id.rl_base_contents)
    private RelativeLayout mBaseContentsLayout;

    @DeclareView(id = R.id.btn_comment, click = "this")
    private View commentButton;

    @DeclareView(id = R.id.btn_share, click = "this")
    private View shareButton;

    @DeclareView(id = R.id.btn_menu, click = "this")
    private View menuButton;

    @DeclareView(id = R.id.ratingbar)
    private RatingBar ratingBar;

    @DeclareView(id = R.id.rl_content_menu)
    private View mContentMenuWrapper;

    @DeclareView(id = R.id.ll_profile_area)
    private View mProfileArea;

    @DeclareView(id = R.id.tv_username)
    private TextView mUsernameLabel;

    @DeclareView(id = R.id.ll_btnset_webview_control)
    private View mWebviewControlButtonset;

    @DeclareView(id = R.id.btn_webview_go_back, click = "this")
    private View mGoBackButton;

    @DeclareView(id = R.id.btn_webview_go_forward, click = "this")
    private View mGoForwardButton;

    @DeclareView(id = R.id.btn_close_panel, click = "this")
    private View mClosePanelButton;

    @DeclareView(id = R.id.progress_content_loading)
    private ProgressBar mContentLoadingProgressBar;

    @DeclareView(id = R.id.iv_toolbar_logo)
    private View mToolbarLogoImage;


    private float mTitleTextSize;
    private float mSubtitleTextSize;
    private float mSmallTitleTextSize;
    private float mSmallSubtitleTextSize;
    private int mDragViewPadding;
    private int mSmallDragViewPadding;
    private int mDragViewHeight;
    private int mSmallDragViewHeight;

    private PopupWindow mMenuPopupWindow;
    private View mContentView;
    private int mContentMenuMaxTop;
    private CommentDialog mCommentDialog;
    private ReadPostRel mReadPostRel;


    public static BaseFragment newInstance(int position) {
        BaseFragment fragment = new ContentViewFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = getContentView(getActivity(), getLayoutResourceId());

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_POSITION)) {
            int position = args.getInt(KEY_POSITION);
            mToolbarLogoImage.setVisibility(View.GONE);
            mPost = PostDataManager.getInstance().getPostAtPosition(position);
        }

        if (mPost == null) {
            // 마지막까지 온 경우
            mContentMenuWrapper.setVisibility(View.GONE);
            mSlidingLayout.setPanelHeight(0);
            mProfileArea.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 21) {
                mContentImageView.setImageDrawable(getResources().getDrawable(R.drawable.empty, null));
            } else {
                mContentImageView.setImageDrawable(getResources().getDrawable(R.drawable.empty));
            }
            return mContentView;
        }

        String imageUrl = mPost.getImageUrl();
        if (imageUrl == null || imageUrl.equals("")) {
            imageUrl = Constants.URLs.NO_IMAGE;
        }

        ImageDisplayUtil.displayImage(imageUrl, mContentImageView);
        mTitleLabel.setText(mPost.getTitle());
        mSubtitleLabel.setText(mPost.getSubtitle());
        mUsernameLabel.setText(mPost.getUser().getDisplayName());


        mSlidingLayout.setDragView(mDragView);
        mSlidingLayout.setPanelSlideListener(this);

        String userProfileImageUrl = mPost.getUser().getDisplayImageUrl();
        ImageDisplayUtil.displayImage(userProfileImageUrl, mUserProfileImageView);

        ratingBar.setRating(mPost.getRating());

        mContentMenuMaxTop = (int) DimensionUtil.dpToPx(60);
        mContentWebView.setOnScrollListener(new ScrollObservableWebView.OnScrollListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {

                if (mContentWebView.getMeasuredContentHeight() > 0
                        && t >= mContentWebView.getMeasuredContentHeight() - mContentWebView.getMeasuredHeight() - 100) {
                    mContentMenuWrapper.setTop(0);
                    return;
                }
                int currentTop = mContentMenuWrapper.getTop();
                int nextTop = Math.min(currentTop + (t - oldt), mContentMenuMaxTop);
                if (nextTop < 0) nextTop = 1;
                mContentMenuWrapper.setTop(nextTop);


            }
        });
        mContentWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mContentLoadingProgressBar.setProgress(newProgress);
            }
        });
        mContentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                ViewAnimateUtil.animateAlpha(
                        mContentLoadingProgressBar,
                        0f, 1f,
                        ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                        null);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ViewAnimateUtil.animateAlpha(
                        mContentLoadingProgressBar,
                        1f, 0f,
                        ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                        new Callable() {
                            @Override
                            public Object call() throws Exception {
                                mContentLoadingProgressBar.setVisibility(View.GONE);
                                return null;
                            }
                        });
            }
        });

        WebSettings settings = mContentWebView.getSettings();

        // 옆으로 화면 벗어나는 이미지 화면에 맞춤
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mContentWebView, true);
        }
        settings.setJavaScriptEnabled(true);

        // TODO : 정리..
        mContentWebView.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            float startX;
            float offset = DimensionUtil.dpToPx(10);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mContentWebView.getScrollY() == 0) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;

                            if (Math.abs(dy) > Math.abs(dx)
                                    && dy > offset) {
                                hideSlideUpPanel();
                            }
                            break;
                    }
                }
                return mContentWebView.onTouchEvent(event);
            }
        });

        mCommentDialog = new CommentDialog(getActivity()).setPostId(mPost.getId());

        calculateViewSizes();
        mBaseContentsLayout.setOnTouchListener(new PanelSlideUpTouchListener(mSlidingLayout));
//        mBaseContentsLayout.setOnTouchListener(new PanelSlideUpTouchListener(mSlidingLayout) {
//            @Override
//            protected void onSwipeUp() {
//                super.onSwipeUp();
//                if (!mSourcePageLoaded) {
//                    loadWebViewContents();
//                }
//            }
//        });

        return mContentView;
    }

    public void setPost(Post post) {
        mPost = post;
    }


    private void loadWebViewContents() {
        mContentWebView.loadUrl(mPost.getSourceUrl());
        mSourcePageLoaded = true;
    }

    private void calculateViewSizes() {
        mTitleTextSize = mTitleLabel.getTextSize();
        mSubtitleTextSize = mSubtitleLabel.getTextSize();
        mSmallTitleTextSize = mTitleTextSize / 2;
        mSmallSubtitleTextSize = mSubtitleTextSize / 2;
        mDragViewPadding = mDragView.getPaddingTop();
        mSmallDragViewPadding = mDragViewPadding / 2;
        mDragViewHeight = mDragView.getLayoutParams().height;
        mSmallDragViewHeight = (int) DimensionUtil.dpToPx(TOOLBAR_HEIGHT_DP);
    }

    public void onChangedToCurrentPage() {
        if (isSlideUpPanelShown()) {
            mContentWebView.onResume();
            mContentWebView.resumeTimers();
        }
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
        EventBus.getDefault().post(new PanelStateChangedEvent(SlidingUpPanelLayout.PanelState.COLLAPSED));
        mContentMenuWrapper.setTop(0);

        mReadPostRel.setLinkClosedTime(System.currentTimeMillis() / 1000);
    }

    @Override
    public void onPanelExpanded(View view) {
        mContentWebView.onResume();
        mContentWebView.resumeTimers();
        setDragViewState(SlidingUpPanelLayout.PanelState.EXPANDED);
        EventBus.getDefault().post(new PanelStateChangedEvent(SlidingUpPanelLayout.PanelState.EXPANDED));
        mLinkClicked = true;
        if (!mSourcePageLoaded) {f
            loadWebViewContents();
        }

        mReadPostRel.setLinkOpenedTime(System.currentTimeMillis() / 1000);

        App.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("content")
                .setAction("view")
                .setLabel("content view: " + mPost.getId())
                .build());
    }

    private void setDragViewState(SlidingUpPanelLayout.PanelState state) {
        if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
            /*
            if (mContentWebView.canGoBack()) {
                mGoBackButton.setVisibility(View.VISIBLE);
            }
            if (mContentWebView.canGoForward()) {
                mGoForwardButton.setVisibility(View.VISIBLE);
            }
            //*/

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
            mGoBackButton.setVisibility(View.GONE);
            mGoForwardButton.setVisibility(View.GONE);
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

        if (isSlideUpPanelShown()) {
            mReadPostRel.setLinkClosedTime(System.currentTimeMillis() / 1000);
        }
        mReadPostRel.setViewEndedTime(System.currentTimeMillis() / 1000);

        mReadPostRel.setRating(ratingBar.getRating());
        mReadPostRel.setSaved(mPost.isSaved());
        mReadPostRel.setLinkClicked(mLinkClicked);
        EventBus.getDefault().post(new PostReadEvent(mReadPostRel));

        mContentWebView.onPause();
        mContentWebView.pauseTimers();
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isSlideUpPanelShown()) {
            setAnimationState(1f);
            setDragViewState(SlidingUpPanelLayout.PanelState.EXPANDED);

            loadWebViewContents();
        }

        mReadPostRel = new ReadPostRel();
        mReadPostRel.setPost(mPost.getId());
        mReadPostRel.setViewStartedTime(System.currentTimeMillis() / 1000);


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
        // webview 열려있는 경우
        if (mSlidingLayout != null
                && mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            if (mContentWebView.canGoBack()) {
                mContentWebView.goBack();
            } else {
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
            return true;
        }

        /* 뒤로 갈 수 있는 경우
        if (mContentWebView.canGoBack()) {
            mContentWebView.goBack();
            return true;
        }
        //*/

        // 팝업 메뉴 열려있는 경우
        if (mMenuPopupWindow != null
                && mMenuPopupWindow.isShowing()) {
            mMenuPopupWindow.dismiss();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                mCommentDialog.getDialog().show();
                break;
            case R.id.btn_share:
                sharePost();
                break;
            case R.id.btn_menu:
                showPopupMenu();
                break;
            case R.id.iv_user_profile:
                Intent intent = ProfileActivity.getIntentWithUserId(getActivity(), mPost.getUser().getId());
                getActivity().startActivity(intent);
                break;
            case R.id.btn_close_panel:
                hideSlideUpPanel();
                break;
            case R.id.btn_webview_go_back:
                if (mContentWebView.canGoBack()) {
                    mContentWebView.goBack();
                }
                break;
            case R.id.btn_webview_go_forward:
                if (mContentWebView.canGoForward()) {
                    mContentWebView.goForward();
                }
                break;
        }
    }

    private void sharePost() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mPost.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.URLs.SHARE + mPost.getId());

        startActivity(Intent.createChooser(shareIntent, null));
    }

    private void showPopupMenu() {
        if (mMenuPopupWindow == null) {
            View popupMenuView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_menu, null);

            // adjust menu position
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int[] menuButtonLocation = new int[2];
            menuButton.getLocationInWindow(menuButtonLocation);
            popupMenuView.setPadding(0, 0, 0, size.y - menuButtonLocation[1]);


            mMenuPopupWindow = new PopupWindow(
                    popupMenuView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            popupMenuView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mMenuPopupWindow.dismiss();
                    }
                    return false;
                }
            });

            ContentMenuItemClickListener listener = new ContentMenuItemClickListener(mPost);
            popupMenuView.findViewById(R.id.btn_report).setOnClickListener(listener);
            popupMenuView.findViewById(R.id.btn_show_tags).setOnClickListener(listener);
            CompoundButton saveToggleButton = ((CompoundButton) popupMenuView.findViewById(R.id.tb_save));
            saveToggleButton.setChecked(mPost.isSaved());
            saveToggleButton.setOnCheckedChangeListener(listener);

        }
        mMenuPopupWindow.showAtLocation(mContentView, Gravity.NO_GRAVITY, 0, 0);
    }


}
