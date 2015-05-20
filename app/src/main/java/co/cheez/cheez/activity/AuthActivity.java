package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DeviceUtil;
import co.cheez.cheez.util.MessageUtil;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @DeclareView(id = R.id.pager)
    ViewPager mViewPager;

    @DeclareView(id = R.id.btn_fb_login, click = "this")
    Button mFacebookLoginButton;

    @DeclareView(id = R.id.btn_skip_login, click = "this")
    Button mSkipLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ViewMapper.inflateLayout(this, this, R.layout.activity_auth));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip_login:
                try {
                    JSONArray devices = new JSONArray()
                            .put(DeviceUtil.getDeviceInfo());
                    JSONObject params = new JSONObject()
                            .put(Constants.Keys.DEVICES, devices);
                    sendSignupRequest(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                    MessageUtil.showDefaultErrorMessage();
                }

                break;
        }
    }

    private void sendSignupRequest(JSONObject params) {
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.URLs.USER,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        try {
                            JSONObject params = new JSONObject()
                                    .put(Constants.Keys.DEVICE, DeviceUtil.getDeviceInfo());
                            sendAuthTokenRequest(params);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showMessage(R.string.error_default);
                        }
                        hideProgressDialog();
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        // TODO : device 중복으로 인한 에러인 경우 그냥 token 요청
                        hideProgressDialog();
                    }
                }
        );
        showProgressDialog();
        App.addRequest(request);
    }

    private void sendAuthTokenRequest(JSONObject params) {
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.URLs.AUTH_TOKEN,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        hideProgressDialog();
                        try {
                            String token = response.getString(Constants.Keys.TOKEN);
                            Auth.getInstance().setAuthToken(token);
                            Intent intent = BaseActivity.getIntent(
                                    AuthActivity.this,
                                    ContentViewActivity.class,
                                    null,
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    );
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        hideProgressDialog();
                    }
                }
        );
        showProgressDialog();
        App.addRequest(request);
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
