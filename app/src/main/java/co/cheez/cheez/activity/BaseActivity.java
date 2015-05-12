package co.cheez.cheez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class BaseActivity extends ActionBarActivity {

    public static Intent getIntent(Context context, Class cls, Bundle args) {
        Intent intent = new Intent(context, cls);
        intent.putExtras(args);

        return intent;
    }


}
