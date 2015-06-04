package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import co.cheez.cheez.App;
import co.cheez.cheez.BuildConfig;
import co.cheez.cheez.R;
import co.cheez.cheez.adapter.ContentViewPagerAdapter;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.event.PanelStateChangedEvent;
import co.cheez.cheez.event.PostReadEvent;
import co.cheez.cheez.fragment.BaseFragment;
import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.model.User;
import co.cheez.cheez.service.RegistrationIntentService;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.MessageUtil;
import co.cheez.cheez.util.ViewAnimateUtil;
import co.cheez.cheez.view.ContentViewPager;
import de.greenrobot.event.EventBus;


public class ContentViewActivity extends BaseActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final int LOAD_OFFSET = 2;
    private static final int LOG_QUEUE_SIZE = 5;

    private JSONArray mReadPostLog;

    ContentViewPagerAdapter mContentViewPagerAdapter;

    @DeclareView(id = R.id.pager)
    ContentViewPager mViewPager;

    @DeclareView(id = R.id.iv_splash)
    ImageView mSplashImageView;

    @DeclareView(id = R.id.rl_btnset_toolbar)
    View mToolbarButtonset;

    @DeclareView(id = R.id.btn_drawer_toggle, click = "this")
    ImageButton mDrawerToggleButton;

    @DeclareView(id = R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @DeclareView(id = R.id.ll_drawer)
    View mDrawerView;

    @DeclareView(id = R.id.btn_upload, click = "this")
    Button mUploadButton;

    @DeclareView(id = R.id.btn_close_drawer, click = "this")
    ImageButton mCloseDrawerButton;

    @DeclareView(id = R.id.rl_drawer_btn_profile, click = "this")
    View mShowProfileButton;

    @DeclareView(id = R.id.iv_drawer_profile)
    ImageView mDrawerProfileImageView;

    @DeclareView(id = R.id.btn_show_saved_contents, click = "this")
    View mShowSavedPostsButton;

    @DeclareView(id = R.id.tv_drawer_username)
    TextView mDrawerUsernameLabel;

    @DeclareView(id = R.id.progress_post_list_loading)
    View mPostListLoadingProgressBar;


    private boolean waintingResponse = false;
    private int nextPage = 1;

    public ContentViewActivity() {
        super();
        mReadPostLog = new JSONArray();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Auth.getInstance().isLogin()) {
            Intent intent = BaseActivity.getIntent(
                    this,
                    AuthActivity.class,
                    null,
                    IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
                            |Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            return;
        }

        // send gcm token to server
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        View contentView = ViewMapper.inflateLayout(this, this, R.layout.activity_content_view);
        setContentView(contentView);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mContentViewPagerAdapter = new ContentViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mContentViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setSwipeEnabledChecker(new ContentViewPager.SwipeEnabledChecker() {
            @Override
            public boolean isSwipeEnabled() {
                try {
                    return !mContentViewPagerAdapter
                            .getActiveFragment(mViewPager.getCurrentItem()).isSlideUpPanelShown();
                } catch (NullPointerException e) {
                    return true;
                }
            }
        });
        mViewPager.setOnPageChangeListener(this);
//        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View page, float position) {
//                page.setAlpha(1 - (Math.abs(position)/2));
//            }
//        });

        // TODO : auth에서 프로필 이미지 가져오기
        User user = Auth.getInstance().getUser();
        if (user != null) {
            ImageDisplayUtil.displayImage(
                    user.getDisplayImageUrl(),
                    mDrawerProfileImageView);
            mDrawerUsernameLabel.setText(user.getDisplayName());
        } else {
            ImageDisplayUtil.displayImage(
                    "drawable://" + R.drawable.ic_launcher,
                    mDrawerProfileImageView);
            mDrawerUsernameLabel.setText(getString(R.string.message_login_required));
        }

        requestPostList();
    }

    private void requestPostList() {
        if (waintingResponse) {
            return;
        }
        mPostListLoadingProgressBar.setVisibility(View.VISIBLE);

        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.POST + "?page=" + nextPage,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        if (BuildConfig.DEBUG) {
                            Log.e("response", response.toString());
                        }
                        try {
                            JSONArray results = response.getJSONArray(Constants.Keys.RESULTS);
                            int resultCount = results.length();
                            for (int i = 0; i < resultCount; i++) {
                                Post post = Post.fromJsonString(results.getString(i));
                                PostDataManager.getInstance().append(post);
                            }

                            if (resultCount > 0) {
                                waintingResponse = false;
                                nextPage ++;
                            } else {
                                PostDataManager.getInstance().append(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                            waintingResponse = false;
                        }
                        if (mSplashImageView.isShown()) {
                            hideSplashView();
                            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        }
                        mPostListLoadingProgressBar.setVisibility(View.GONE);

                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        mPostListLoadingProgressBar.setVisibility(View.GONE);
                        MessageUtil.showDefaultErrorMessage();
                        waintingResponse = false;
                    }
                }
        );
        waintingResponse = true;
        App.addRequest(request, Constants.Integers.TIMEOUT_LONG);
    }

    private void hideSplashView() {
        ViewAnimateUtil.animateAlpha(mSplashImageView,
                1f, 0f,
                ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        mSplashImageView.setVisibility(View.GONE);
                        return null;
                    }
                });
    }

    public void hideToolbar() {
        hideToolbar(ViewAnimateUtil.ANIMATION_DURATION_DEFAULT);
    }
    public void hideToolbar(long duration) {
        ViewAnimateUtil.animateAlpha(
                mToolbarButtonset,
                mToolbarButtonset.getAlpha(),
                0f,
                duration,
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        mToolbarButtonset.setVisibility(View.GONE);
                        return null;
                    }
                }
        );
    }

    public void showToolbar() {
        showToolbar(ViewAnimateUtil.ANIMATION_DURATION_DEFAULT);
    }
    public void showToolbar(long duration) {
        mToolbarButtonset.setVisibility(View.VISIBLE);
        ViewAnimateUtil.animateAlpha(
                mToolbarButtonset,
                mToolbarButtonset.getAlpha(),
                1f,
                duration,
                null
        );
    }


    public void onEvent(PostReadEvent event) {
        try {
            mReadPostLog.put(event.data.toJsonObject());
            Log.e("asdf", event.data.toJsonString());
            if (mReadPostLog.length() >= LOG_QUEUE_SIZE) {
                sendReadPostLog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(PanelStateChangedEvent event) {
        switch (event.mPanelState) {
            case EXPANDED:
                hideToolbar(0);
                break;
            case COLLAPSED:
                showToolbar();
                break;
        }
    }

    @Override
    protected void onResume() {
        if (mContentViewPagerAdapter != null) {
            mContentViewPagerAdapter.notifyDataSetChanged();
        }
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        sendReadPostLog();
    }

    private void sendReadPostLog() {
        if (mReadPostLog.length() > 0) {
            JSONArray readPostLog = mReadPostLog;
            mReadPostLog = new JSONArray();
            try {
                JSONObject params = new JSONObject()
                        .put(Constants.Keys.DATA, readPostLog);
                // send request
                Request request = new AuthorizedRequest(
                        Request.Method.POST,
                        Constants.URLs.READ_POST,
                        params,
                        new DefaultListener(),
                        new DefaultErrorListener());
                App.addRequest(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onDestroy() {
        PostDataManager.getInstance().clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        BaseFragment currentFragment
                = mContentViewPagerAdapter.getActiveFragment(mViewPager.getCurrentItem());
        if (currentFragment == null) {
            super.onBackPressed();
        } else if (!currentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ContentViewFragment currentFragment = mContentViewPagerAdapter.getActiveFragment(position);
        currentFragment.onChangedToCurrentPage();
        if (currentFragment.isSlideUpPanelShown()) {
            hideToolbar();
        } else {
            showToolbar();
        }

        // load more posts
        if (position >= mContentViewPagerAdapter.getCount() - LOAD_OFFSET) {
            requestPostList();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                startActivity(BaseActivity.getIntent(this, ContentUploadActivity.class, null));
                break;
            case R.id.btn_close_drawer:
                mDrawerLayout.closeDrawer(mDrawerView);
                break;
            case R.id.btn_drawer_toggle:
                if (mDrawerLayout.isDrawerOpen(mDrawerView)) {
                    mDrawerLayout.closeDrawer(mDrawerView);
                } else {
                    mDrawerLayout.openDrawer(mDrawerView);
                }
                break;
            case R.id.rl_drawer_btn_profile:
                User user = Auth.getInstance().getUser();
                if (user != null) {
                    Intent intent
                            = ProfileActivity.getIntentWithUserId(this, user.getId());
                    startActivity(intent);
                } else {
                    MessageUtil.showMessage(R.string.message_login_required);
                }
                break;
            case R.id.btn_show_saved_contents:
                Intent savedPostListActivityIntent
                        = BaseActivity.getIntent(this, SavedPostListActivity.class, null);
                startActivity(savedPostListActivityIntent);
                break;
        }
    }




}
