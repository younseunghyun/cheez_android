package co.cheez.cheez.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import co.cheez.cheez.R;
import co.cheez.cheez.util.ImageDisplayUtil;

/**
 * Created by jiho on 5/29/15.
 */
public class ImageListViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImageView;

    public ImageListViewHolder(View itemView) {
        super(itemView);
        mImageView = (ImageView) itemView.findViewById(R.id.iv_upload_image_list);
    }

    public void setImageUrl(String imageUrl) {
        ImageDisplayUtil.displayImage(imageUrl, mImageView);
    }
}
