package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.cheez.cheez.automation.view.ViewMapper;


/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseFragment extends Fragment {

    /**
     *
     * @return Layout resource id
     */
    protected abstract int getLayoutRes();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return ViewMapper.inflateLayout(getActivity(), this, getLayoutRes());
    }
}
