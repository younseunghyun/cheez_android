package co.cheez.cheez.util;

import android.widget.Toast;

import co.cheez.cheez.App;
import co.cheez.cheez.R;

/**
 * Created by jiho on 5/17/15.
 */
public class MessageUtil {
    public static void showDefaultErrorMessage() {
        showMessage(R.string.error_default);
    }

    public static void showMessage(int stringResourceId, int duration) {
        Toast.makeText(App.getContext(), stringResourceId, duration).show();
    }

    public static void showMessage(int stringResourceId) {
        showMessage(stringResourceId, Toast.LENGTH_SHORT);
    }

    public static void showMessage(CharSequence message, int duration) {
        Toast.makeText(App.getContext(), message, duration).show();
    }

    public static void showMessage(CharSequence message) {
        showMessage(message, Toast.LENGTH_SHORT);
    }
}
