package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.adapter.ContentRecyclerViewAdapter;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class SavedPostListFragment extends BaseFragment implements View.OnClickListener {

    @DeclareView(id = R.id.btn_finish, click = "this")
    Button mFinishButton;

    @DeclareView(id = R.id.rv_post_list)
    RecyclerView mPostListRecyclerView;

    @DeclareView(id = R.id.progress_post_loading)
    View mPostLoadingProgressView;

    @DeclareView(id = R.id.tv_empty_label)
    View mEmptyLabel;

    ContentRecyclerViewAdapter mContentRecyclerViewAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);


        mContentRecyclerViewAdapter = new ContentRecyclerViewAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPostListRecyclerView.setLayoutManager(mLayoutManager);
        mPostListRecyclerView.setAdapter(mContentRecyclerViewAdapter);

        sendPostListRequest();

        return contentView;
    }

    private void sendPostListRequest() {
        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.SAVED_POST,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        mPostLoadingProgressView.setVisibility(View.GONE);

                        try {
                            JSONArray results = response.getJSONArray(Constants.Keys.RESULTS);
                            int length = results.length();
                            List<Post> postList = mContentRecyclerViewAdapter.getPostList();
                            for (int i = 0; i < length; i++) {
                                postList.add(Post.fromJsonString(results.getString(i)));
                            }
                            mContentRecyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            MessageUtil.showDefaultErrorMessage();
                            e.printStackTrace();
                        }
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        mPostLoadingProgressView.setVisibility(View.GONE);
                    }
                }
        );
        mPostLoadingProgressView.setVisibility(View.VISIBLE);
        App.addRequest(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                break;
        }
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_saved_post_list;
    }
}
