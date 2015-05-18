package co.cheez.cheez.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import co.cheez.cheez.automation.lifecycle.LifecycleObserver;
import co.cheez.cheez.event.PostAddedEvent;
import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import de.greenrobot.event.EventBus;

/**
 * Created by jiho on 5/14/15.
 */
public class ContentViewPagerAdapter extends FragmentPagerAdapter
        implements LifecycleObserver {
    public ContentViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ArrayList<Post> getPostList() {
        return PostDataManager.getInstance().getPostList();
    }

    @Override
    public Fragment getItem(int position) {
        return ContentViewFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return getPostList().size();
    }

    @Override
    public void onResume() {
        notifyDataSetChanged();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PostAddedEvent event) {
        notifyDataSetChanged();
    }
}
