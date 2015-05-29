package co.cheez.cheez.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.cheez.cheez.R;
import co.cheez.cheez.view.holder.ImageListViewHolder;

/**
 * Created by jiho on 5/29/15.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListViewHolder> {
    private List<String> mImageUrlList;

    public ImageListAdapter() {
        super();
        mImageUrlList = new ArrayList<>();
    }

    @Override
    public ImageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_image, null);
        return new ImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageListViewHolder holder, int position) {

        holder.setImageUrl(mImageUrlList.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageUrlList.size();
    }
}
