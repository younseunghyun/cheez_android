package co.cheez.cheez.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import co.cheez.cheez.App;

/**
 * Created by jiho on 5/24/15.
 */
public class ImageDisplayUtil {
    public static void displayImage(String imageUri, ImageView targetImageView) {
        ImageLoader.getInstance().displayImage(
                imageUri,
                targetImageView,
                App.getDefaultImageDisplayOption()
        );
    }
}
