package co.cheez.cheez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import co.cheez.cheez.R;
import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.model.Post;

public class SingleContentViewActivity extends BaseActivity {

    private static final String KEY_POST = "post";

    public static Intent getIntent(Context context, Post post) {
        Intent intent = new Intent(context, SingleContentViewActivity.class);
        intent.putExtra(KEY_POST, post.toJsonString());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_content_view);

        Intent intent = getIntent();
        Post post = Post.fromJsonString(intent.getStringExtra(KEY_POST));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ContentViewFragment fragment = new ContentViewFragment();
        fragment.setPost(post);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }
}
