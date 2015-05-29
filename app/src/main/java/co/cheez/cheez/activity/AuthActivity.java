package co.cheez.cheez.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookSdk;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Locale;

import co.cheez.cheez.R;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.fragment.LoginFragment;
import co.cheez.cheez.fragment.TutorialFragment;
import co.cheez.cheez.util.ViewAnimateUtil;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @DeclareView(id = R.id.pager)
    ViewPager mViewPager;

    @DeclareView(id = R.id.indicator)
    CirclePageIndicator mPageIndicator;

    @DeclareView(id = R.id.btn_skip_tutorial, click = "this")
    View mSkipTutorialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(ViewMapper.inflateLayout(this, this, R.layout.activity_auth));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mPageIndicator.setFillColor(getResources().getColor(R.color.theme_primary));
        mPageIndicator.setViewPager(mViewPager);
        mPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    ViewAnimateUtil.animateAlpha(
                            mSkipTutorialButton,
                            mSkipTutorialButton.getAlpha(), 0f,
                            ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                            null
                    );

                } else {
                    ViewAnimateUtil.animateAlpha(
                            mSkipTutorialButton,
                            mSkipTutorialButton.getAlpha(), 1f,
                            ViewAnimateUtil.ANIMATION_DURATION_DEFAULT,
                            null
                    );
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip_tutorial:
                mViewPager.setCurrentItem(4);
                break;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 4) {
                return TutorialFragment.newInstance(getTutorialImageResId(position));
            } else {
                return new LoginFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    private int getTutorialImageResId(int position) {
        switch (position) {
            case 0:
                return R.drawable.tutorial1;
            case 1:
                return R.drawable.tutorial2;
            case 2:
                return R.drawable.tutorial3;
            case 3:
                return R.drawable.tutorial4;
        }
        return 0;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_auth, container, false);
            return rootView;
        }
    }

}
