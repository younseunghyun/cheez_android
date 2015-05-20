package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Auth.getInstance().isLogin()) {
            Intent intent = BaseActivity.getIntent(
                    this,
                    AuthActivity.class,
                    null,
                    Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP
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


        // 너중에 지우자
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BaseActivity.getIntent(ContentViewActivity.this, ContentUploadActivity.class, null));
            }
        });
    }

    private void requestPostList() {
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
                            for (int i = 0; i < results.length(); i++) {
                                Post post = Post.fromJsonString(results.getString(i));
                                PostDataManager.getInstance().append(post);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }

                        hideSplashView();
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        MessageUtil.showDefaultErrorMessage();
                    }
                }
        );
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
        if (currentFragment.isSlideUpPanelShown()) {
            hideToolbar();
        } else {
            showToolbar();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
