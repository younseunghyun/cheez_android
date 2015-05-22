package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.adapter.ContentViewPagerAdapter;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.fragment.BaseFragment;
import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;
import co.cheez.cheez.util.ViewAnimateUtil;


public class ContentViewActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    ContentViewPagerAdapter mContentViewPagerAdapter;

    @DeclareView(id = R.id.pager)
    ViewPager mViewPager;

    @DeclareView(id = R.id.iv_splash)
    ImageView mSplashImageView;

    @DeclareView(id = R.id.btn_upload)
    Button mUploadButton;

    @DeclareView(id = R.id.rl_btnset_toolbar)
    View mToolbarButtonset;

    private boolean waintingResponse = false;
    private int LOAD_OFFSET = 2;

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

        View contentView = ViewMapper.inflateLayout(this, this, R.layout.activity_content_view);
        setContentView(contentView);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mContentViewPagerAdapter = new ContentViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mContentViewPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        requestPostList();
    }

    private void requestPostList() {
        if (waintingResponse) {
            return;
        }
        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.POST,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        try {
                            JSONArray results = response.getJSONArray(Constants.Keys.RESULTS);
                            int resultCount = results.length();
                            for (int i = 0; i < resultCount; i++) {
                                Post post = Post.fromJsonString(results.getString(i));
                                PostDataManager.getInstance().append(post);
                            }


                            if (resultCount > 0) {
                                waintingResponse = false;
                            } else {
                                // TODO : 결과 수가 0이면 다음 요청 추가로 보내지 않도록 막고 더미 포스트 추가하기
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                            waintingResponse = false;
                        }
                        if (mSplashImageView.isShown()) {
                            hideSplashView();
                        }


                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
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



    @Override
    protected void onResume() {
        if (mContentViewPagerAdapter != null) {
            mContentViewPagerAdapter.notifyDataSetChanged();
        }
        super.onResume();
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
}
