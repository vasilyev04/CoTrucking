package com.kiparisov.cotrucking.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kiparisov.cotrucking.ui.vpfragments.TransportersFragment;
import com.kiparisov.cotrucking.ui.vpfragments.CargoFragment;

public class VPHomeAdapter extends FragmentPagerAdapter {
    private Context context;
    private int totalTabs;

    public VPHomeAdapter(Context context, FragmentManager fm, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TransportersFragment();
            case 1:
                return new CargoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
