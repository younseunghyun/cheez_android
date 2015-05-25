package co.cheez.cheez.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.cheez.cheez.R;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.util.ImageDisplayUtil;

/**
 * Created by jiho on 5/24/15.
 */
public class PostListItemViewHolder extends RecyclerView.ViewHolder {
    private Post mPost;

    @DeclareView(id = R.id.iv_post_image)
    private ImageView mPostImageView;

    @DeclareView(id = R.id.tv_post_title)
    private TextView mPostTitleLabel;

    @DeclareView(id = R.id.tv_post_subtitle)
    private TextView mPostSubtitleLabel;

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
    }
}
