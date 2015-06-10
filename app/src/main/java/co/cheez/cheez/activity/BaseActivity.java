package co.cheez.cheez.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.HashSet;
import java.util.Iterator;

import co.cheez.cheez.App;
import co.cheez.cheez.automation.lifecycle.LifecycleObservable;
import co.cheez.cheez.automation.lifecycle.LifecycleObserver;
import co.cheez.cheez.util.DialogUtil;


public abstract class BaseActivity extends ActionBarActivity
        implements LifecycleObservable {
    protected ProgressDialog mProgressDialog;
    protected HashSet<LifecycleObserver> mLifecycleObservers;

    public BaseActivity() {
        super();
        mLifecycleObservers = new HashSet<>();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showProgressDialog() {
        showProgressDialog(false);
    }

    protected void showProgressDialog(boolean cancellable) {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.getProgressDialog(
                    this,
                    cancellable
            );
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void addObserver(LifecycleObserver observer) {
        mLifecycleObservers.add(observer);
    }

    public void removeObserver(LifecycleObserver observer) {
        mLifecycleObservers.remove(observer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Iterator<LifecycleObserver> iterator = mLifecycleObservers.iterator();
        while(iterator.hasNext()) {
            iterator.next().onResume();
        }
        App.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        App.setCurrentActivity(null);
        Iterator<LifecycleObserver> iterator = mLifecycleObservers.iterator();
        while(iterator.hasNext()) {
            iterator.next().onPause();
        }
        super.onPause();
    }
}
