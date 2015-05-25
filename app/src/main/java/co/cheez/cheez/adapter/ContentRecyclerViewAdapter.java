package co.cheez.cheez.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.view.holder.PostListItemViewHolder;

/**
 * Created by jiho on 5/24/15.
 */
public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<PostListItemViewHolder> {
    private List<Post> mPostList;

    public ContentRecyclerViewAdapter() {
        mPostList = new ArrayList<>();
    }

    @Override
    public PostListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.listitem_post, null);
        return new PostListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostListItemViewHolder postListItemViewHolder, int i) {
        postListItemViewHolder.setPost(mPostList.get(i));
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public List<Post> getPostList() {
        return mPostList;
    }
}
