package com.kiparisov.cotrucking.ui.bottomfragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.FragmentProfileBinding;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.User;
import com.kiparisov.cotrucking.ui.EditProfileActivity;

import java.util.Locale;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private User user;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private static final int RESULT_AVATAR_DELETED = 3;

    private void registerCallBack(){
        editProfileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (!TextUtils.isEmpty(Auth.getCurrentUser().getAvatarUri())){
                    Glide.with(ProfileFragment.this)
                            .load(Uri.parse(Auth.getCurrentUser().getAvatarUri()))
                            .centerCrop()
                            .into(binding.imageAvatar);
                }else{
                    binding.imageAvatar.setImageDrawable(getResources().getDrawable(R.drawable.unnamed));
                }
                fillViews();
            }
        });
    }


    private void init(){
        user = Auth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        registerCallBack();

        init();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(requireContext().getResources().getColor(R.color.profile_fragment_status_bar));
        }

        binding.btnEdit.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        if (!TextUtils.isEmpty(user.getAvatarUri())){
            Glide.with(this)
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .into(binding.imageAvatar);
        }else{
            binding.imageAvatar.setImageDrawable(requireContext().getDrawable(R.drawable.unnamed));
        }

        fillViews();
    }


    private void fillViews(){
        if(!TextUtils.isEmpty(user.getName())){
            binding.tvUserName.setText(user.getName());
            binding.tvUserName.setTextColor(Color.BLACK);
        }else{
            binding.tvUserName.setText(getString(R.string.default_user_name));
            binding.tvUserName.setTextColor(Color.BLACK);
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
        }else{
            binding.tvAge.setText(getString(R.string.profile_not_filled));
            binding.tvAge.setTextColor(Color.GRAY);
        }
        if (!TextUtils.isEmpty(user.getProfileDescription())){
            binding.tvAboutMe.setText(user.getProfileDescription());
            binding.tvAboutMe.setTextColor(Color.BLACK);
        }else{
            binding.tvAboutMe.setText(getString(R.string.profile_not_filled));
            binding.tvAboutMe.setTextColor(Color.GRAY);
        }
        binding.tvEmail.setText(user.getEmail());
        binding.tvEmail.setTextColor(Color.BLACK);
        if (!TextUtils.isEmpty(user.getPhone())){
            binding.tvPhone.setText(user.getPhone());
            binding.tvPhone.setTextColor(Color.BLACK);
        }else {
            binding.tvPhone.setText(getString(R.string.profile_not_filled));
            binding.tvPhone.setTextColor(Color.GRAY);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(requireContext().getResources().getColor(R.color.status_bar_color));
        }
    }




}