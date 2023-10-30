package com.kiparisov.cotrucking.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.adapters.MyAdsRecyclerViewAdapter;
import com.kiparisov.cotrucking.databinding.ActivityMyAdsBinding;
import com.kiparisov.cotrucking.interfaces.OnDataAdsReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.model.TransporterAd;

import java.util.ArrayList;
import java.util.Locale;

public class MyAdsActivity extends AppCompatActivity {
    private ActivityMyAdsBinding binding;
    private ArrayList<Ad> ads;
    private MyAdsRecyclerViewAdapter adapter;
    private int selectedItemId;
    private ActivityResultLauncher<Intent> showAdActivityResultLauncher;
    private boolean isActivityVisible;

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private void registerCallBack(){
        showAdActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intent = new Intent();
                    intent.putExtra("selectedItemId", selectedItemId);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    private void init(){
        registerCallBack();

        ads = new ArrayList<>();
        adapter = new MyAdsRecyclerViewAdapter(ads, new OnItemClickListener() {
            @Override
            public void onClickFavoriteButton(Ad ad, ImageButton favorite, int position) {
                //skipped
            }

            @Override
            public void onItemClickListener(Ad ad, int position) {
                Intent intent;
                if (ad instanceof CargoAd) {
                    intent = new Intent(MyAdsActivity.this, EditCargoAdActivity.class);
                } else {
                    TransporterAd transporterAd = (TransporterAd) ad;
                    if (transporterAd.getAdType() == TransporterAd.AD_CAR_TYPE){
                        intent = new Intent(MyAdsActivity.this, EditCarAdActivity.class);
                    }else{
                        intent = new Intent(MyAdsActivity.this, EditTruckAdActivity.class);
                    }
                }
                intent.putExtra(Constants.INTENT_EXTRA_AD_KEY, ad.getKey());
                showAdActivityResultLauncher.launch(intent);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(MyAdsActivity.this);
        binding.recyclerViewMyAds.setLayoutManager(manager);
        binding.recyclerViewMyAds.setAdapter(adapter);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        init();

        Intent intent = getIntent();
        selectedItemId = (int) intent.getExtras().get("selectedItemId");

        if (Auth.getCurrentUser().getUserAds().isEmpty()){
            showWelcomeText();
        }else{
            getAds();
        }
    }



    private void getAds(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.adIcon.setVisibility(View.GONE);
        binding.tvWelcome.setVisibility(View.GONE);
        DataBaseHelper.getUserAds(new OnDataAdsReceivedListener<Ad>() {
            @Override
            public void onAdsReceived(ArrayList<Ad> resultArr) {
                ads.clear();
                for (int i = resultArr.size() - 1; i >= 0; i--) {
                    ads.add(resultArr.get(i));
                }
                Log.d("TAG", "onAdsReceived: " + ads.size());
                if (isActivityVisible){
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerViewMyAds.setVisibility(View.VISIBLE);
                    binding.tvHelp.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    //если список пользовательских объявлений пуст, то показать кликабельный текст
    private void showWelcomeText(){
        binding.adIcon.setVisibility(View.VISIBLE);
        binding.tvHelp.setVisibility(View.INVISIBLE);
        binding.recyclerViewMyAds.setVisibility(View.INVISIBLE);
        binding.tvWelcome.setVisibility(View.VISIBLE);
        SpannableString string = new SpannableString(this
                .getString(R.string.my_ads_welcome_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(MyAdsActivity.this, AddAdActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint clickableStr) {
                clickableStr.setColor(getResources().getColor(R.color.main_color));
            }
        };

        int start, end;
        if (Locale.getDefault().getLanguage().equals("ru")){
            start = 41;
            end = 79;
        }else{
            start = 22;
            end = 48;
        }
        string.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        binding.tvWelcome.setText(string);
        binding.tvWelcome.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("selectedItemId", selectedItemId);
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("selectedItemId", selectedItemId);
        setResult(RESULT_CANCELED, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}