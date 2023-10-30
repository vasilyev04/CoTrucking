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
import com.kiparisov.cotrucking.databinding.ActivityShowAdTransporterBinding;
import com.kiparisov.cotrucking.interfaces.OnDataAdReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnDataUserReceivedListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.model.TransporterAd;
import com.kiparisov.cotrucking.model.User;

import java.util.ArrayList;

public class ShowAdTransporterActivity extends AppCompatActivity {
    private ActivityShowAdTransporterBinding binding;
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
        binding = ActivityShowAdTransporterBinding.inflate(getLayoutInflater());
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
        getTransporterAd(key);

        binding.btnShowNumber.setOnClickListener(vew -> {
            Button btnShowNumber = binding.btnShowNumber;
            if (!isPhoneShowed){
                btnShowNumber.setBackgroundResource(R.drawable.custom_toggle_button_on);
                if (!TextUtils.isEmpty(userOwner.getPhone())){
                    btnShowNumber.setText(userOwner.getPhone());
                }else{
                    btnShowNumber.setText(ShowAdTransporterActivity.this.getString(R.string.no_phone));
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
            Intent showUserProfile = new Intent(ShowAdTransporterActivity.this,
                    ShowProfileActivity.class);
            showUserProfile.putExtra("key", givenAd.getOwnerKey());
            startActivity(showUserProfile);
        });

        binding.btnShowNumber.setOnLongClickListener(view -> {
            if (isPhoneShowed && !TextUtils.isEmpty(userOwner.getPhone())){
                Intent startPhoneCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                        userOwner.getPhone(),
                        null));
                startActivity(startPhoneCall);
            }
            return false;
        });

        binding.btnWrite.setOnClickListener(view -> {
            Intent openChat = new Intent(ShowAdTransporterActivity.this, ChatActivity.class);
            openChat.putExtra("companionKey", userOwner.getKey());
            startActivity(openChat);
        });

        binding.btnFab.setOnClickListener(view -> {
            Intent openChat = new Intent(ShowAdTransporterActivity.this, ChatActivity.class);
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


    private void fillViews(TransporterAd ad){
        if (ad.isFavorite()){
            binding.btnAddToFavorite.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_main_color24));
        }
        binding.title.setText(ad.getTitle());
        binding.tvTransporterLoadCapacity
                .setText(":    " + ad.getLoadCapacity() + " " + " " + getString(R.string.weight) + " " + getString(R.string.transporter_end_tv_load_capacity));
        if (ad.getAdType() == TransporterAd.AD_CAR_TYPE){
            binding.imageTransporterType.setImageDrawable(getDrawable(R.drawable.ic_baseline_directions_car_24));
        }

        if (ad.getAdType() == TransporterAd.AD_TRUCK_TYPE){
            switch (ad.getTruckType()){
                case TransporterAd.TRUCK_TYPE_AWNING:
                    binding.tvTruckType.setText(getString(R.string.truck_type_tent));
                    break;
                case TransporterAd.TRUCK_TYPE_CLOSED:
                    binding.tvTruckType.setText(R.string.truck_type_closed);
                    break;
                default:
                    binding.tvTruckType.setText(R.string.truck_type_open);
            }

        }else{
            binding.titleTruckType.setVisibility(View.GONE);
            binding.tvTruckType.setVisibility(View.GONE);
        }
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




    private void getTransporterAd(String key){
        DataBaseHelper.getTransporterAd(key, new OnDataAdReceivedListener() {
            @Override
            public void onAdReceived(Ad ad) {
                givenAd = ad;
                Log.d("TAG", "onCreate: " + givenAd.getKey());

                Auth.getUserByKey(givenAd.getOwnerKey(), new OnDataUserReceivedListener() {
                    @Override
                    public void onUserReceived(User user) {
                        userOwner = user;
                        if (isActivityVisible){
                            binding.btnAddToFavorite.setClickable(true);
                            binding.mainLayout.setVisibility(View.VISIBLE);
                            binding.btnFab.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            fillViews((TransporterAd) ad);
                            if (!TextUtils.isEmpty(ad.getImageUri())){
                                Glide.with(ShowAdTransporterActivity.this)
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