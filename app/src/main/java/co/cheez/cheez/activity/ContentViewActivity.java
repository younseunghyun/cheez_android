package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.adapter.ContentViewPagerAdapter;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;
import co.cheez.cheez.util.ViewAnimateUtil;


public class ContentViewActivity extends BaseActivity {

    ContentViewPagerAdapter mSectionsPagerAdapter;

    @DeclareView(id=R.id.pager)
    ViewPager mViewPager;

    @DeclareView(id=R.id.iv_splash)
    ImageView mSplashImageView;

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
        }

        View contentView = ViewMapper.inflateLayout(this, this, R.layout.activity_content_view);
        setContentView(contentView);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ContentViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        addObserver(mSectionsPagerAdapter);

        requestPostList();
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
        App.getRequestQueue().add(request);
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
}
