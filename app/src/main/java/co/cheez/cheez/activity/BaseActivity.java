package co.cheez.cheez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public abstract class BaseActivity extends ActionBarActivity {

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


}
