package co.cheez.cheez.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Iterator;

import co.cheez.cheez.automation.lifecycle.LifecycleObservable;
import co.cheez.cheez.automation.lifecycle.LifecycleObserver;
import co.cheez.cheez.automation.view.HasLayout;
import co.cheez.cheez.automation.view.ViewMapper;


/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseFragment extends Fragment
        implements HasLayout, LifecycleObservable {

    protected HashSet<LifecycleObserver> mLifecycleObservers;

    public BaseFragment() {
        super();
        mLifecycleObservers = new HashSet<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getContentView(getActivity(), getLayoutResourceId());
    }

    protected View getContentView(Context context, int layoutResId) {
        return ViewMapper.inflateLayout(context, this, layoutResId);
    }

    public void addObserver(LifecycleObserver observer) {
        mLifecycleObservers.add(observer);
    }

    public void removeObserver(LifecycleObserver observer) {
        mLifecycleObservers.remove(observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        Iterator<LifecycleObserver> iterator = mLifecycleObservers.iterator();
        while(iterator.hasNext()) {
            iterator.next().onResume();
        }
    }

    @Override
    public void onPause() {
        Iterator<LifecycleObserver> iterator = mLifecycleObservers.iterator();
        while(iterator.hasNext()) {
            iterator.next().onPause();
        }
        super.onPause();
    }
}
