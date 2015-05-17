package co.cheez.cheez.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import co.cheez.cheez.R;


public abstract class BaseActivity extends ActionBarActivity {
    protected ProgressDialog mProgressDialog;

    public static Intent getIntent(Context context, Class cls, Bundle args) {
        Intent intent = new Intent(context, cls);
        if (args != null) {
            intent.putExtras(args);
        }

        return intent;
    }

    public static Intent getIntent(Context context, Class cls, Bundle args, int flags) {
        Intent intent = getIntent(context, cls, args);
        intent.addFlags(flags);

        return intent;
    }

    protected void showProgressDialog() {
        showProgressDialog(false);
    }

    protected void showProgressDialog(boolean cancellable) {
        showProgressDialog(R.string.app_name,
                R.string.message_progress_default,
                cancellable);
    }

    protected void showProgressDialog(int titleResourceId, int messageResourceId, boolean cancellable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        } else if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.setTitle(titleResourceId);
        mProgressDialog.setMessage(getString(messageResourceId));
        mProgressDialog.setCancelable(cancellable);
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
