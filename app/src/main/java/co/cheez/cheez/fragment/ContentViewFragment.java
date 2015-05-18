package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.cheez.cheez.R;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;

/**
 * Created by jiho on 5/13/15.
 */
public class ContentViewFragment extends BaseFragment {
    public static final String KEY_POSITION = "position";

    public static Fragment newInstance(int position) {
        Fragment fragment = new ContentViewFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        Post post = PostDataManager.getInstance().getPostAtPosition(position);

        // TODO : set view contents

        return contentView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_content_view;
    }
}
