package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Callable;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.adapter.ContentRecyclerViewAdapter;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.User;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.MessageUtil;
import co.cheez.cheez.util.ViewAnimateUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends BaseFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @DeclareView(id = R.id.tv_toolbar_username)
    private TextView mUsernameLabel;

    @DeclareView(id = R.id.rv_post_list)
    private RecyclerView mPostListRecyclerView;

    @DeclareView(id = R.id.iv_profile_image)
    private ImageView mProfileImageView;

    @DeclareView(id = R.id.tv_cheez_count)
    private TextView mCheezCountLabel;

    @DeclareView(id = R.id.progress_post_loading)
    private View mPostLoadingProgressView;

    @DeclareView(id = R.id.tv_empty_label)
    private View mEmptyLabel;

    @DeclareView(id = R.id.btn_finish, click = "this")
    private View mFinishButton;

    @DeclareView(id = R.id.btn_edit_profile, click = "this")
    private View mEditProfileButton;

    @DeclareView(id = R.id.tb_follow)
    private ToggleButton mFollowToggleButton;

    @DeclareView(id = R.id.tv_follower_count)
    private TextView mFollowerCountLabel;

    @DeclareView(id = R.id.tv_followee_count)
    private TextView mFolloweeCountLabel;



    private ContentRecyclerViewAdapter mPostListAdapter;
    private User mUser;
    private int nextPage = 1;
    private boolean waitingResponse = false;
    private LinearLayoutManager mLayoutManager;

    public static Fragment newInstance(Bundle args) {
        Fragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_profile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        long userId = args.getLong(Constants.Keys.USER_ID);

        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        User loggedUser = Auth.getInstance().getUser();

        if (userId == 0
                || (loggedUser != null && userId == loggedUser.getId())) {
            // 내 프로필
            mEditProfileButton.setVisibility(View.VISIBLE);
        } else {
            mFollowToggleButton.setVisibility(View.VISIBLE);
        }

        mPostListAdapter = new ContentRecyclerViewAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPostListRecyclerView.setAdapter(mPostListAdapter);
        mPostListRecyclerView.setLayoutManager(mLayoutManager);
        mPostListRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mLayoutManager.findLastVisibleItemPosition()
                        == mPostListAdapter.getItemCount() - 1) {
                    loadPostList();
                }
            }
        });

        loadUserData(userId);

        return contentView;
    }

    protected void setUser(User user) {
        mUser = user;

        ImageDisplayUtil.displayImage(user.getDisplayImageUrl(), mProfileImageView);
        mUsernameLabel.setText(user.getDisplayName());
        Log.e(getString(R.string.cheez_count), String.format(getString(R.string.cheez_count), mUser.getUploadCount()));
        mCheezCountLabel.setText(
                String.format(getString(R.string.cheez_count), mUser.getUploadCount()));
        mFolloweeCountLabel.setText(
                String.format(getString(R.string.followee_count),  mUser.getFolloweeCount()));
        mFollowerCountLabel.setText(
                String.format(getString(R.string.follower_count),  mUser.getFollowerCount()));

        if (user.isFollowing()) {
            mFollowToggleButton.setChecked(true);
        }

        mFollowToggleButton.setOnCheckedChangeListener(this);
    }

    private void loadUserData(long userId) {
        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.USER + userId,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        ((BaseActivity) getActivity()).hideProgressDialog();
                        setUser(User.fromJsonObject(response));
                        loadPostList();
                        Log.e("userdata", response.toString());
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((BaseActivity) getActivity()).hideProgressDialog();
                    }
                }
        );
        ((BaseActivity) getActivity()).showProgressDialog();
        App.addRequest(request);
    }

    private void loadPostList() {
        if (waitingResponse) {
            return;
        }
        waitingResponse = true;
        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.POST + "?user_id=" + mUser.getId() + "&page=" + nextPage,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        hidePostLoadingProgressView();
                        try {
                            JSONArray results = response.getJSONArray(Constants.Keys.RESULTS);
                            int length = results.length();

                            List<Post> postList = mPostListAdapter.getPostList();

                            for (int i = 0; i < length; i++) {
                                postList.add(Post.fromJsonObject(results.getJSONObject(i)));
                            }
                            mPostListAdapter.notifyDataSetChanged();
                            if (mPostListAdapter.getItemCount() == 0) {
                                mEmptyLabel.setVisibility(View.VISIBLE);
                            }

                            if (length > 0) {
                                nextPage ++;
                                waitingResponse = false;
                            }
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
                        hidePostLoadingProgressView();
                    }
                }
        );

        showPostLoadingProgressView();
        App.addRequest(request);
    }

    private void showPostLoadingProgressView() {
        mPostLoadingProgressView.setVisibility(View.VISIBLE);
        ViewAnimateUtil.animateAlpha(
                mPostLoadingProgressView,
                0f, 1f,
                ViewAnimateUtil.ANIMATION_DURATION_SHORT,
                null);
    }

    private void hidePostLoadingProgressView() {
        ViewAnimateUtil.animateAlpha(
                mPostLoadingProgressView,
                1f, 0f,
                ViewAnimateUtil.ANIMATION_DURATION_SHORT,
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        mPostLoadingProgressView.setVisibility(View.GONE);
                        return null;
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                break;
            case R.id.btn_edit_profile:

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        try {
            JSONObject params = new JSONObject().put(Constants.Keys.USER_ID, mUser.getId());
            if (isChecked) {
                mUser.setFollowerCount(mUser.getFolloweeCount() + 1);
            } else {
                mUser.setFollowerCount(mUser.getFolloweeCount() - 1);
                params.put(Constants.Keys.DELETE, true);
            }
            mFolloweeCountLabel.setText(
                    String.format(getString(R.string.follower_count),  mUser.getFollowerCount()));
            Request request = new AuthorizedRequest(
                    Request.Method.POST,
                    Constants.URLs.FOLLOW,
                    params,
                    new DefaultListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            super.onResponse(response);
                            ((BaseActivity) getActivity()).hideProgressDialog();

                        }
                    },
                    new DefaultErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);
                            if (isChecked) {
                                mUser.setFollowerCount(mUser.getFolloweeCount() - 1);
                            } else {
                                mUser.setFollowerCount(mUser.getFolloweeCount() + 1);
                            }
                            mFollowerCountLabel.setText(
                                    String.format(getString(R.string.follower_count),  mUser.getFollowerCount()));

                            mFollowToggleButton.setOnCheckedChangeListener(null);
                            mFollowToggleButton.setChecked(!isChecked);
                            mFollowToggleButton.setOnCheckedChangeListener(ProfileFragment.this);

                            ((BaseActivity) getActivity()).hideProgressDialog();
                        }
                    }
            );
            ((BaseActivity) getActivity()).showProgressDialog();
            App.addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
