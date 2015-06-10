package co.cheez.cheez.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.cheez.cheez.event.PostAddedEvent;
import co.cheez.cheez.event.PostRemoveEvent;
import co.cheez.cheez.fragment.BaseFragment;
import co.cheez.cheez.fragment.ContentViewFragment;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.model.PostDataManager;
import de.greenrobot.event.EventBus;

/**
 * Created by jiho on 5/14/15.
 */
public class ContentViewPagerAdapter extends FragmentPagerAdapter {
    private Map<Integer, ContentViewFragment> mActiveFragmentsMap;

    public ContentViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mActiveFragmentsMap = new HashMap<>();
        EventBus.getDefault().register(this);
    }

    public ArrayList<Post> getPostList() {
        return PostDataManager.getInstance().getPostList();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = ContentViewFragment.newInstance(position);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ContentViewFragment item = (ContentViewFragment) super.instantiateItem(container, position);
        mActiveFragmentsMap.put(position, item);
        return item;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mActiveFragmentsMap.remove(position);
    }

    @Override
    public int getCount() {
        return getPostList().size();
    }

    public ContentViewFragment getActiveFragment(int position) {
        return mActiveFragmentsMap.get(position);
    }

    public void onEvent(PostAddedEvent event) {
        notifyDataSetChanged();
    }
    public void onEvent(PostRemoveEvent event) {
        notifyDataSetChanged();
    }
}
