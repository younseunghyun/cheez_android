package co.cheez.cheez.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.cheez.cheez.automation.view.HasLayout;
import co.cheez.cheez.automation.view.ViewMapper;


/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseFragment extends Fragment implements HasLayout {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getContentView(getActivity(), getLayoutResourceId());
    }

    protected View getContentView(Context context, int layoutResId) {
        return ViewMapper.inflateLayout(context, this, layoutResId);
    }
}
