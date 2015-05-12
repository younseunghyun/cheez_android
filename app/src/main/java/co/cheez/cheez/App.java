package co.cheez.cheez;

import android.app.Application;
import android.content.Context;

/**
 * Created by jiho on 5/10/15.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
