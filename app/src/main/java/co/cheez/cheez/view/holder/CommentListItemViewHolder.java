package co.cheez.cheez.view.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.cheez.cheez.R;
import co.cheez.cheez.activity.ProfileActivity;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.model.Comment;
import co.cheez.cheez.util.ImageDisplayUtil;

/**
 * Created by jiho on 5/25/15.
 */
public class CommentListItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private Comment mComment;

    @DeclareView(id = R.id.iv_user_profile, click = "this")
    ImageView mUserProfileImageView;

    @DeclareView(id = R.id.tv_username)
    TextView mUsernameLabel;

    @DeclareView(id = R.id.tv_commented_time)
    TextView mCommentedTimeLabel;

    @DeclareView(id = R.id.tv_comment_body)
    TextView mCommentBody;

    public CommentListItemViewHolder(View itemView) {
        super(itemView);

        ViewMapper.mapLayout(this, itemView);
    }

    public void setComment(Comment comment) {
        mComment = comment;

        ImageDisplayUtil.displayImage(mComment.getUser().getDisplayImageUrl(), mUserProfileImageView);
        mUsernameLabel.setText(mComment.getUser().getDisplayName());
        mCommentBody.setText(mComment.getComment());
        mCommentedTimeLabel.setText(mComment.getDisplayTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_profile:
                Intent intent = ProfileActivity.getIntentWithUserId(
                        v.getContext(),
                        mComment.getUser().getId()
                );
                v.getContext().startActivity(intent);
                break;
        }
    }
}
