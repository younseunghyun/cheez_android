package co.cheez.cheez.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;

import co.cheez.cheez.App;

/**
 * Created by jiho on 5/24/15.
 */
public class ImageDisplayUtil {
    public static void displayImage(String imageUri, ImageView targetImageView) {
        if (imageUri.endsWith(".gif")) {
            Glide.with(App.getContext())
                    .load(imageUri)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(targetImageView);
        } else {
            ImageLoader.getInstance().displayImage(
                    imageUri,
                    targetImageView,
                    App.getDefaultImageDisplayOption()
            );
        }
    }
}
