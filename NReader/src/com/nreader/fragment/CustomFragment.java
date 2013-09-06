
package com.nreader.fragment;

import android.support.v4.app.Fragment;

public class CustomFragment extends Fragment implements ICustomFragment {

    @Override
    public void onResume() {
        super.onResume();
        onResumeFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onResumeFragment();
    }

    @Override
    public void onResumeFragment() {
    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
    }
}
