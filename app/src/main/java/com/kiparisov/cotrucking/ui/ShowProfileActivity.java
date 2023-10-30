package com.kiparisov.cotrucking.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.ActivityShowProfileBinding;
import com.kiparisov.cotrucking.interfaces.OnDataUserReceivedListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.User;

public class ShowProfileActivity extends AppCompatActivity {
    private ActivityShowProfileBinding binding;
    private User user;
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
        binding = ActivityShowProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String profileUserKey = intent.getStringExtra("key");
        initUser(profileUserKey);

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void initUser(String key){
        binding.relativeLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        Auth.getUserByKey(key, new OnDataUserReceivedListener() {
            @Override
            public void onUserReceived(User receivedUser) {
                user = receivedUser;
                if (isActivityVisible){
                    fillViews();
                    binding.relativeLayout.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }



    private void fillViews(){
        if(!TextUtils.isEmpty(user.getName())){
            binding.tvUserName.setText(user.getName());
            binding.tvUserName.setTextColor(Color.BLACK);
        }
        if (!TextUtils.isEmpty(user.getAvatarUri())){
            Glide.with(this)
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .into(binding.imageAvatar);
        }else{
            binding.imageAvatar.setImageDrawable(this.getDrawable(R.drawable.unnamed));
        }
        if (!TextUtils.isEmpty(user.getAvatarUri())){
            Glide.with(this)
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .into(binding.imageAvatar);
        }

        switch (user.getUserIndicator()){
            case User.CLIENT_INDICATOR:
                binding.tvUserIndicator.setText(getString(R.string.user_indicator_client));
                break;
            case User.TRANSPORTER_INDICATOR:
                binding.tvUserIndicator.setText(getString(R.string.user_indicator_transporter));
                break;
            default:
                binding.tvUserIndicator.setText(getString(R.string.user_indicator_administration));
        }

        if (user.getAge() != 0){
            binding.tvAge.setText(user.getAge() + "");
            binding.tvAge.setTextColor(Color.BLACK);
        }
        if (!TextUtils.isEmpty(user.getProfileDescription())){
            binding.tvAboutMe.setText(user.getProfileDescription());
            binding.tvAboutMe.setTextColor(Color.BLACK);
        }

        if (!user.isEmailPrivate()) {
            binding.tvEmail.setText(user.getEmail());
            binding.tvEmail.setTextColor(Color.BLACK);
        }
        if (!user.isPhonePrivate() && !TextUtils.isEmpty(user.getPhone())){
            binding.tvPhone.setText(user.getPhone());
            binding.tvPhone.setTextColor(Color.BLACK);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}