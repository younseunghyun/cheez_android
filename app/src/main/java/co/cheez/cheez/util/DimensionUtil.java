package co.cheez.cheez.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import co.cheez.cheez.App;

/**
 * Created by jiho on 5/20/15.
 */
public class DimensionUtil {
    public static float dpToPx(float dp) {
        Resources resources = App.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
