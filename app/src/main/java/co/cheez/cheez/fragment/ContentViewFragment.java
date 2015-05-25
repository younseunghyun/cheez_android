package co.cheez.cheez.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.pkmmte.view.CircularImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.ContentViewActivity;
import co.cheez.cheez.activity.ProfileActivity;
import co.cheez.cheez.automation.view.ContentPopupMenu;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ScrollObservableWebView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DimensionUtil;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.ViewAnimateUtil;
import co.cheez.cheez.view.listener.ContentMenuItemClickListener;
import co.cheez.cheez.view.listener.PanelSlideUpTouchListener;

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
    private Button commentButton;

    @DeclareView(id = R.id.btn_share, click = "this")
    private Button shareButton;

    @DeclareView(id = R.id.btn_menu, click = "this")
    private Button menuButton;

    @DeclareView(id = R.id.ratingbar)
    private RatingBar ratingBar;

    @DeclareView(id = R.id.rl_content_menu)
    private View mContentMenuWrapper;

    @DeclareView(id = R.id.ll_profile_area)
    private View mProfileArea;

    @DeclareView(id = R.id.tv_username)
    private TextView mUsernameLabel;

    private float mTitleTextSize;
    private float mSubtitleTextSize;
    private float mSmallTitleTextSize;
    private float mSmallSubtitleTextSize;
    private int mDragViewPadding;
    private int mSmallDragViewPadding;
    private int mDragViewHeight;
    private int mSmallDragViewHeight;

    private PopupMenu mPopupMenu;
    private PopupWindow mMenuPopupWindow;
    private View mContentView;
    private int mContentMenuMaxTop;


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
        int position = args.getInt(KEY_POSITION);
        mPost = PostDataManager.getInstance().getPostAtPosition(position);

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

        ImageDisplayUtil.displayImage(mPost.getImageUrl(), mContentImageView);
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
                Log.e("progress", newProgress + "");
            }
        });
        mContentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mContentWebView.getSettings().setJavaScriptEnabled(true);

        // TODO : 정리..
        mContentWebView.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            float offset = DimensionUtil.dpToPx(10);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mContentWebView.getScrollY() == 0) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float currentY = event.getY();
                            if (currentY - startY > offset) {
                                hideSlideUpPanel();
                            }
                            break;
                    }
                }
                return mContentWebView.onTouchEvent(event);
            }
        });

        // set popup menu
        mPopupMenu = new ContentPopupMenu(getActivity(), menuButton, mPost.getId());


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
        ((ContentViewActivity)getActivity()).showToolbar();
        mContentMenuWrapper.setTop(0);
    }

    @Override
    public void onPanelExpanded(View view) {
        mContentWebView.onResume();
        mContentWebView.resumeTimers();
        setDragViewState(SlidingUpPanelLayout.PanelState.EXPANDED);
        ((ContentViewActivity)getActivity()).hideToolbar(0);
        mLinkClicked = true;
        if (!mSourcePageLoaded) {
            loadWebViewContents();
        }
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
                    getResources().getColor(R.color.theme_secondary),
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
                    .put(Constants.Keys.RATING, ratingBar.getRating())
                    .put(Constants.Keys.SAVED, mPost.isSaved())
                    .put(Constants.Keys.LINK_CLICKED, mLinkClicked);
            Request request = new AuthorizedRequest(
                    Request.Method.POST,
                    Constants.URLs.READ_POST,
                    params,
                    new DefaultListener(),
                    new DefaultErrorListener()
            );
            App.addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:

                break;
            case R.id.btn_share:

                break;
            case R.id.btn_menu:
                showPopupMenu();
                break;
            case R.id.iv_user_profile:
                Intent intent = ProfileActivity.getIntentWithUserId(getActivity(), mPost.getUser().getId());
                getActivity().startActivity(intent);
                break;
        }
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
