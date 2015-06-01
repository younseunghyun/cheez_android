package co.cheez.cheez.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.cheez.cheez.R;
import co.cheez.cheez.model.Comment;
import co.cheez.cheez.view.holder.CommentListItemViewHolder;

/**
 * Created by jiho on 5/25/15.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentListItemViewHolder> {
    private List<Comment> mCommentList;

    public CommentRecyclerViewAdapter() {
        mCommentList = new ArrayList<>();
    }

    @Override
    public CommentListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_comment, parent, false);
        return new CommentListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentListItemViewHolder holder, int position) {
        holder.setComment(mCommentList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public List<Comment> getCommentList() {
        return mCommentList;
    }
}
