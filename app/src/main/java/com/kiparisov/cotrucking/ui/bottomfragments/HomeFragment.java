package com.kiparisov.cotrucking.ui.bottomfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.FragmentHomeBinding;
import com.kiparisov.cotrucking.adapters.VPHomeAdapter;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        createViewPager();
    }


    //создание вью пейджера
    private void createViewPager(){
        TabLayout tbLayout = binding.tbSwitcher;
        ViewPager viewPager = binding.vpAd;

        //((AppCompatActivity)getActivity()).setSupportActionBar(binding.homeToolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_home));


        VPHomeAdapter vpHomeAdapter = new VPHomeAdapter(getContext(), getChildFragmentManager(), tbLayout.getTabCount());
        viewPager.setAdapter(vpHomeAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tbLayout));

        tbLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}