package com.kiparisov.cotrucking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityShowAdCargoBinding;
import com.kiparisov.cotrucking.interfaces.OnDataAdReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnDataUserReceivedListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.model.User;

import java.util.ArrayList;

public class ShowAdCargoActivity extends AppCompatActivity {
    private ActivityShowAdCargoBinding binding;
    private Ad givenAd;
    private User userOwner;
    private boolean isPhoneShowed = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAdCargoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnAddToFavorite.setClickable(false);
        binding.mainLayout.setVisibility(View.GONE);
        binding.btnFab.setVisibility(View.GONE);


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);

        Intent intent = getIntent();
        String key = intent.getExtras().getString(Constants.INTENT_EXTRA_AD_KEY);
        Log.d("TAG", "onCreate: " + key);
        getCargoAd(key);


        binding.btnShowNumber.setOnClickListener(vew -> {
            Button btnShowNumber = binding.btnShowNumber;
            if (!isPhoneShowed){
                btnShowNumber.setBackgroundResource(R.drawable.custom_toggle_button_on);
                if (!TextUtils.isEmpty(userOwner.getPhone())){
                    btnShowNumber.setText(userOwner.getPhone());
                }else{
                    btnShowNumber.setText(ShowAdCargoActivity.this.getString(R.string.no_phone));
                }
                isPhoneShowed = true;
            }else{
                btnShowNumber.setText(getString(R.string.phone));
                btnShowNumber.setBackgroundResource(R.drawable.custom_toggle_button_off);
                isPhoneShowed = false;
            }
        });

        binding.btnAddToFavorite.setOnClickListener(view -> {
            if (givenAd.isFavorite()){
                givenAd.setFavorite(false);
                ArrayList<String> favoriteAds = Auth.getCurrentUser().getFavoritesAds();
                favoriteAds.remove(givenAd.getKey());
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                binding.btnAddToFavorite.setImageDrawable(
                        this.getDrawable(R.drawable.ic_baseline_favorite_border_24));
            }else{
                givenAd.setFavorite(true);
                Auth.getCurrentUser().addFavoritesAds(givenAd.getKey());
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                binding.btnAddToFavorite.setImageDrawable(
                        this.getDrawable(R.drawable.ic_baseline_favorite_main_color24));
            }
        });

        binding.imageAvatar.setOnClickListener(view -> {
            Intent showUserProfile = new Intent(ShowAdCargoActivity.this,
                    ShowProfileActivity.class);
            showUserProfile.putExtra("key", givenAd.getOwnerKey());
            startActivity(showUserProfile);
        });

        binding.btnShowNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isPhoneShowed && !TextUtils.isEmpty(userOwner.getPhone())){
                    Intent startPhoneCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                            userOwner.getPhone(),
                            null));
                    startActivity(startPhoneCall);
                }
                return false;
            }
        });


        binding.btnWrite.setOnClickListener(view -> {
            Intent openChat = new Intent(ShowAdCargoActivity.this, ChatActivity.class);
            openChat.putExtra("companionKey", userOwner.getKey());
            startActivity(openChat);
        });

        binding.btnFab.setOnClickListener(view -> {
            Intent openChat = new Intent(ShowAdCargoActivity.this, ChatActivity.class);
            openChat.putExtra("companionKey", userOwner.getKey());
            startActivity(openChat);
        });

        binding.btnReport.setOnClickListener(view -> {
            Intent startReportActivity = new Intent(this, ReportActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("adKey", givenAd.getKey());
            bundle.putString("ownerKey", givenAd.getOwnerKey());
            startReportActivity.putExtras(bundle);
            startActivity(startReportActivity);
        });


    }



    private void fillViews(CargoAd ad){
        if (ad.isFavorite()){
            binding.btnAddToFavorite.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_main_color24));
        }
        binding.title.setText(ad.getTitle());
        binding.tvCargoVolume.setText(":   " + ad.getVolume() + " " + getString(R.string.volume));
        binding.tvCargoWeight.setText(":   " + ad.getWeight() + " " + getString(R.string.weight));
        String dimensions;
        switch (ad.getDimensions()){
            case CargoAd.DIMENSIONS_SMALL:
                dimensions = getString(R.string.cargo_type_small);
                break;
            case CargoAd.DIMENSIONS_MEDIUM:
                dimensions = getString(R.string.cargo_type_medium);
                break;
            default:
                dimensions = getString(R.string.cargo_type_large);
        }
        binding.tvDimensions.setText(dimensions);
        binding.tvDescription.setText(ad.getDescription());
        binding.tvStartLocation.setText(ad.getStartLocation());
        binding.tvEndLocation.setText(ad.getEndLocation());
        if (!TextUtils.isEmpty(userOwner.getAvatarUri())){
            Glide.with(this)
                    .load(userOwner.getAvatarUri())
                    .centerCrop()
                    .into(binding.imageAvatar);
        }
        binding.tvUserName.setText(userOwner.getName());
    }


    private void getCargoAd(String key){
        DataBaseHelper.getCargoAd(key, new OnDataAdReceivedListener() {
            @Override
            public void onAdReceived(Ad ad) {
                givenAd = ad;
                Log.d("TAG", "onAdReceived: " + ad.getKey());

                Auth.getUserByKey(givenAd.getOwnerKey(), new OnDataUserReceivedListener() {
                    @Override
                    public void onUserReceived(User user) {
                        userOwner = user;
                        if (isActivityVisible){
                            binding.btnAddToFavorite.setClickable(true);
                            binding.mainLayout.setVisibility(View.VISIBLE);
                            binding.btnFab.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            fillViews((CargoAd) ad);
                            if (!TextUtils.isEmpty(ad.getImageUri())){
                                Glide.with(ShowAdCargoActivity.this)
                                        .load(ad.getImageUri())
                                        .into(binding.headerImage);
                            }else{
                                binding.headerImage.setImageDrawable(getDrawable(R.drawable.no_image));
                            }
                        }

                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}