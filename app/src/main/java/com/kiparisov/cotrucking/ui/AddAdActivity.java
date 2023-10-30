package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityAddAdBinding;
import com.kiparisov.cotrucking.adapters.VPAdsAdapter;

public class AddAdActivity extends AppCompatActivity {


    private ActivityAddAdBinding binding;
    private int itemIdFromActivityStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.addAddToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);


        itemIdFromActivityStarted = getIntent().getIntExtra("selectedItemId", R.id.home);

        createViewPager();
    }


    //создание вью пейджера
    private void createViewPager(){
        TabLayout tbLayout = binding.tbSwitcher;
        ViewPager viewPager = binding.vpAd;


        VPAdsAdapter vpAdsAdapter = new VPAdsAdapter(this, getSupportFragmentManager(), tbLayout.getTabCount());
        viewPager.setAdapter(vpAdsAdapter);

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
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("selectedItemId", itemIdFromActivityStarted);
        setResult(RESULT_OK, i);

        super.onBackPressed();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent();
        i.putExtra("selectedItemId", itemIdFromActivityStarted);
        setResult(RESULT_OK, i);
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}