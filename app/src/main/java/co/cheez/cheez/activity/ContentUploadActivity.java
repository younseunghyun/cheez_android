package co.cheez.cheez.activity;

import android.content.Intent;
import android.os.Bundle;

import co.cheez.cheez.R;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.fragment.ContentUploadFragment;

public class ContentUploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_upload);

        String sourceUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (sourceUrl != null) {
            if (!Auth.getInstance().isLogin()) {
                Intent intent = BaseActivity.getIntent(
                        this,
                        AuthActivity.class,
                        null,
                        Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP
                );
                startActivity(intent);
                return;
            }

            ContentUploadFragment fragment
                    = (ContentUploadFragment) getSupportFragmentManager()
                                              .findFragmentById(R.id.fragment);
            fragment.setSourceUrl(sourceUrl);
        }

    }
}
