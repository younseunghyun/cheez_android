package co.cheez.cheez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import co.cheez.cheez.R;

public class ProfileEditActivity extends BaseActivity {


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
    }

}
