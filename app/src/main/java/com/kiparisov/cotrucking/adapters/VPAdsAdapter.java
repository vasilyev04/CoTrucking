package com.kiparisov.cotrucking.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kiparisov.cotrucking.ui.vpfragments.AddAdCargoFragment;
import com.kiparisov.cotrucking.ui.vpfragments.AddAdTransporterFragment;

public class VPAdsAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public VPAdsAdapter(Context context, @NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AddAdCargoFragment();
            case 1:
                return new AddAdTransporterFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
