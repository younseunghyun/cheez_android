package co.cheez.cheez.view.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.activity.SingleContentViewActivity;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import co.cheez.cheez.model.User;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.ImageDisplayUtil;

/**
 * Created by jiho on 5/24/15.
 */
public class PostListItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private Post mPost;


    @DeclareView(id = R.id.ll_listitem_root, click = "this")
    private View listitemRootView;

    @DeclareView(id = R.id.iv_post_image)
    private ImageView mPostImageView;

    @DeclareView(id = R.id.tv_post_title)
    private TextView mPostTitleLabel;

    @DeclareView(id = R.id.tv_post_subtitle)
    private TextView mPostSubtitleLabel;

    @DeclareView(id = R.id.tv_like_count)
    private TextView mLikeCountLabel;

    @DeclareView(id = R.id.tv_comment_count)
    private TextView mCommentCountLabel;

    @DeclareView(id = R.id.tv_view_count)
    private TextView mViewCountLabel;

    @DeclareView(id = R.id.btn_delete_post, click = "this")
    private View mPostDeleteButton;

    public PostListItemViewHolder(View view) {
        super(view);
        ViewMapper.mapLayout(this, view);
    }

    public void setPost(Post post) {
        mPost = post;

        mPostImageView.setImageDrawable(null);
        ImageDisplayUtil.displayImage(mPost.getImageUrl(), mPostImageView);
        mPostTitleLabel.setText(mPost.getTitle());
        mPostSubtitleLabel.setText(mPost.getSubtitle());

        mLikeCountLabel.setText(mPost.getDisplayLikeCount());
        mCommentCountLabel.setText(" " + mPost.getDisplayCommentCount());
        mViewCountLabel.setText(" " + mPost.getDisplayViewCount());

        User user = Auth.getInstance().getUser();
        if (user != null && user.getId() == mPost.getUser().getId()) {
            mPostDeleteButton.setVisibility(View.VISIBLE);
        } else {
            mPostDeleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_listitem_root:
                Intent intent = SingleContentViewActivity.getIntent(v.getContext(), mPost);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                break;
            case R.id.btn_delete_post:
                ((BaseActivity) App.getCurrentActivity()).showProgressDialog();
                Request request = new AuthorizedRequest(
                        Request.Method.DELETE,
                        Constants.URLs.POST + mPost.getId() + "/",
                        null,
                        new DefaultListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                super.onResponse(response);
                                PostDataManager.getInstance().remove(mPost);
                                Log.e("response", response.toString());
                                ((BaseActivity) App.getCurrentActivity()).hideProgressDialog();
                            }
                        },
                        new DefaultErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                super.onErrorResponse(error);
                                ((BaseActivity) App.getCurrentActivity()).hideProgressDialog();
                            }
                        }
                );
                App.addRequest(request);
                break;
        }
    }
}
