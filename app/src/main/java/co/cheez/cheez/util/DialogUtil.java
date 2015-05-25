package co.cheez.cheez.util;

import android.app.ProgressDialog;
import android.content.Context;

import co.cheez.cheez.R;

/**
 * Created by jiho on 5/25/15.
 */
public class DialogUtil {

    public static ProgressDialog getProgressDialog(Context context, boolean cancellable) {
        return getProgressDialog(
                context,
                R.string.app_name,
                R.string.message_progress_default,
                cancellable);
    }
    public static ProgressDialog getProgressDialog(
            Context context,
            int titleResourceId,
            int messageResourceId,
            boolean cancellable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(titleResourceId);
        dialog.setMessage(context.getString(messageResourceId));
        dialog.setCancelable(cancellable);

        return dialog;
    }
}
