package com.kiparisov.cotrucking.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.ui.bottomfragments.ChatFragment;
import com.kiparisov.cotrucking.ui.bottomfragments.FavoritesFragment;
import com.kiparisov.cotrucking.ui.bottomfragments.ProfileFragment;
import com.kiparisov.cotrucking.databinding.ActivityFragmentContainerBinding;
import com.kiparisov.cotrucking.ui.bottomfragments.HomeFragment;

public class FragmentContainerActivity extends AppCompatActivity {
    private ActivityFragmentContainerBinding binding;

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;
    private int prevItemIdSelected;
    private ActivityResultLauncher<Intent> addActivityResultLauncher;
    private ActivityResultLauncher<Intent> myAdsActivityResultLauncher;
    private DrawerLayout dw;

    private long exitTouchTime;
    private Toast exitToast;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTouchTime < 3000){
            exitToast.cancel();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }else{
            exitTouchTime = System.currentTimeMillis();
            exitToast = Toast.makeText(this, getString(R.string.exit_toast), Toast.LENGTH_SHORT);
            exitToast.show();
        }
    }



    private void registerCallBack(){
        addActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent data = result.getData();
                    int itemId =  (int) data.getExtras().get("selectedItemId");
                    binding.bottomNavMenu.setSelectedItemId(itemId);
                }
            }
        });

        myAdsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment()).commit();
                    binding.bottomNavMenu.setSelectedItemId(R.id.home);
                }else{
                    Intent data = result.getData();
                    int itemId =  (int) data.getExtras().get("selectedItemId");
                    binding.bottomNavMenu.setSelectedItemId(itemId);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerCallBack();

        homeFragment = new HomeFragment();
        favoritesFragment = new FavoritesFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();


        //по запуску приложения будет запущен фрагмент с всеми объявлениями
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();
        prevItemIdSelected = R.id.home;

        //меняем фрагменты в контейнере взависимости от выбранного пункта меню
        binding.bottomNavMenu.setOnItemSelectedListener(item -> {
            //запоминаем выбранный пункт меню, чтобы он был выбран после возвращения с AddAdActivity
            if(item.getItemId() == R.id.add){
                Intent intent = new Intent(this, AddAdActivity.class);
                intent.putExtra("selectedItemId", prevItemIdSelected);
                addActivityResultLauncher.launch(intent);
                return true;
            }

            Fragment fragment;
            switch (item.getItemId()){
                case R.id.favorites:
                    fragment = favoritesFragment;
                    prevItemIdSelected = item.getItemId();
                break;
                case R.id.messages:
                    fragment = chatFragment;
                    prevItemIdSelected = item.getItemId();
                    break;
                case R.id.profile:
                    fragment = profileFragment;
                    prevItemIdSelected = item.getItemId();
                    break;
                default:
                    fragment = homeFragment;
                    prevItemIdSelected = item.getItemId();

            }
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.fragment_container, fragment, "fragment").commit();
            return true;
        });


        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(getString(R.string.title_home));
        setSupportActionBar(toolbar);

        //приветсвие пользователя в burger menu
        View header = binding.navView.getHeaderView(0);
        ImageView imageView = header.findViewById(R.id.image_avatar);

        //клик по аватарке в burger menu
        imageView.setOnClickListener(view -> {
            Fragment fragment = profileFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment, "fragment").commit();
            binding.bottomNavMenu.setSelectedItemId(R.id.profile);
            prevItemIdSelected = R.id.profile;
            binding.drawerLayout.close();
        });

        TextView tv = header.findViewById(R.id.user_email);

        if (!TextUtils.isEmpty(Auth.getCurrentUser().getName())){
            tv.setText(Auth.getCurrentUser().getName());
        }else{
            tv.setText(Auth.getCurrentUser().getEmail());
        }

        if (!TextUtils.isEmpty(Auth.getCurrentUser().getAvatarUri())){
            Glide.with(this)
                    .load(Auth.getCurrentUser().getAvatarUri())
                    .centerCrop()
                    .into(imageView);
        }

        dw = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dw, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        dw.addDrawerListener(toggle);
        toggle.syncState();



        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //запоминаем выбранный пункт меню, чтобы он был выбран после возвращения с AddAdActivity
                if(item.getItemId() == R.id.add){
                    Intent intent = new Intent(FragmentContainerActivity.this, AddAdActivity.class);
                    intent.putExtra("selectedItemId", prevItemIdSelected);
                    dw.close();
                    addActivityResultLauncher.launch(intent);
                    return false;
                }

                if (item.getItemId() == R.id.my_ads){
                    Intent intent = new Intent(FragmentContainerActivity.this, MyAdsActivity.class);
                    intent.putExtra("selectedItemId", prevItemIdSelected);
                    dw.close();
                    myAdsActivityResultLauncher.launch(intent);
                    return false;
                }

                if (item.getItemId() == R.id.about){
                    Intent intent = new Intent(FragmentContainerActivity.this, AboutInfoActivity.class);
                    intent.putExtra("selectedItemId", prevItemIdSelected);
                    dw.close();
                    myAdsActivityResultLauncher.launch(intent);
                    return false;
                }

                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.favorites:
                        fragment = favoritesFragment;
                        prevItemIdSelected = item.getItemId();
                        break;
                    case R.id.messages:
                        fragment = chatFragment;
                        prevItemIdSelected = item.getItemId();
                        break;
                    case R.id.profile:
                        fragment = profileFragment;
                        prevItemIdSelected = item.getItemId();
                        break;
                    case R.id.sign_out:
                        Auth.signOutInSharedPref(FragmentContainerActivity.this);
                        Auth.auth.signOut();
                        Intent intent = new Intent(FragmentContainerActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    default:
                        fragment = homeFragment;
                        prevItemIdSelected = item.getItemId();

                }
                binding.bottomNavMenu.setSelectedItemId(prevItemIdSelected);
                FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, fragment, "fragment").commit();
                dw.close();
                return false;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //если пользователь сменил аватарку, то она сразу отобразится в бургерном меню
        View header = binding.navView.getHeaderView(0);
        ImageView imageView = header.findViewById(R.id.image_avatar);
        if (!TextUtils.isEmpty(Auth.getCurrentUser().getAvatarUri())){
            Glide.with(this)
                    .load(Auth.getCurrentUser().getAvatarUri())
                    .centerCrop()
                    .into(imageView);
        }else{
            imageView.setImageDrawable(getDrawable(R.drawable.unnamed));
        }

        //если пользователь сменил имя
        TextView name = header.findViewById(R.id.user_email);
        if (!TextUtils.isEmpty(Auth.getCurrentUser().getName())){
            name.setText(Auth.getCurrentUser().getName());
        }else{
            name.setText(Auth.getCurrentUser().getEmail());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}