package co.cheez.cheez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import co.cheez.cheez.R;
import co.cheez.cheez.fragment.ProfileFragment;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;

public class ProfileActivity extends BaseActivity {

    public static Intent getIntentWithUserId(Context context, long userId) {
        Bundle args = new Bundle();
        args.putLong(Constants.Keys.USER_ID, userId);
        return BaseActivity.getIntent(context, ProfileActivity.class, args);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        long userId = intent.getLongExtra(Constants.Keys.USER_ID, 0);
        if (userId == 0) {
            MessageUtil.showDefaultErrorMessage();
            finish();
        } else {
            Bundle args = new Bundle();
            args.putLong(Constants.Keys.USER_ID, userId);
            Fragment fragment = ProfileFragment.newInstance(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, fragment);
            transaction.commit();
        }
    }


}
