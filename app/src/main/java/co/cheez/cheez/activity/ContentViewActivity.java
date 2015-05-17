package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import co.cheez.cheez.R;
import co.cheez.cheez.adapter.ContentViewPagerAdapter;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;


public class ContentViewActivity extends BaseActivity {

    FragmentPagerAdapter mSectionsPagerAdapter;

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
    }
}
