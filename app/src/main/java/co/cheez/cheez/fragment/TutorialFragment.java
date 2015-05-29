package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import co.cheez.cheez.R;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.util.ImageDisplayUtil;

/**
 * Created by jiho on 5/29/15.
 */
public class TutorialFragment extends BaseFragment {
    public static final String KEY_TUTORIAL_IMAGE_RES_ID = "tutorial_image_res_id";

    @DeclareView(id = R.id.iv_tutorial)
    ImageView mTutorialImageView;



    public static TutorialFragment newInstance(int tutorialImageResId) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TUTORIAL_IMAGE_RES_ID, tutorialImageResId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        int imageResId = getArguments().getInt(KEY_TUTORIAL_IMAGE_RES_ID);
        ImageDisplayUtil.displayImage("drawable://" + imageResId, mTutorialImageView);
        return contentView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_tutorial;
    }
}
