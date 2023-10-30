package com.kiparisov.cotrucking.ui.bottomfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.adapters.FavoritesRecyclerViewAdapter;
import com.kiparisov.cotrucking.databinding.FragmentFavoritesBinding;
import com.kiparisov.cotrucking.interfaces.OnDataAdsReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.ui.ShowAdCargoActivity;
import com.kiparisov.cotrucking.ui.ShowAdTransporterActivity;
import java.util.ArrayList;
import java.util.Locale;


public class FavoritesFragment extends Fragment {
    private FirebaseDatabase db;
    private FragmentFavoritesBinding binding;
    private DatabaseReference userFavoritesAds;
    private ArrayList<Ad> ads;
    private FavoritesRecyclerViewAdapter adapter;
    private ValueEventListener listener;
    private boolean isFirstLaunch = true;

    @Override
    public void onResume() {
        super.onResume();
        userFavoritesAds.removeEventListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        userFavoritesAds.addValueEventListener(listener);
    }

    private void init(){
        isFirstLaunch = true;
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        SharedPreferences sp = requireContext().getSharedPreferences(Constants.SP_USER_SIGN_KEY,
                Context.MODE_PRIVATE);
        userFavoritesAds = db.getReference(Constants.FB_USERS_REFERENCE_KEY)
                .child(sp.getString("KEY", null)).child("favoritesAds");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> favoritesAds = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getValue(String.class);
                    favoritesAds.add(key);
                }
                Auth.getCurrentUser().setFavoritesAds(favoritesAds);
                if (Auth.getCurrentUser().getFavoritesAds().isEmpty()) {
                    showWelcomeText();
                }else {
                    getAds();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(getLayoutInflater());

        init();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_favorites));

        ads = new ArrayList<>();
        adapter = new FavoritesRecyclerViewAdapter(ads, new OnItemClickListener() {
            @Override
            public void onClickFavoriteButton(Ad ad, ImageButton favorite, int position) {
                ad.setFavorite(false);
                ArrayList<String> favoriteAds = Auth.getCurrentUser().getFavoritesAds();
                favoriteAds.remove(ad.getKey());
                Auth.updateUserInFireBase(Auth.getCurrentUser());
                ads.remove(ad);

                if (ads.isEmpty()) {
                    showWelcomeText();
                    adapter.notifyDataSetChanged();
                }else {
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onItemClickListener(Ad ad, int position) {
                Intent intent;
                if (ad instanceof CargoAd) {
                    intent = new Intent(requireContext(), ShowAdCargoActivity.class);
                } else {
                    intent = new Intent(requireContext(), ShowAdTransporterActivity.class);
                }
                intent.putExtra(Constants.INTENT_EXTRA_AD_KEY, ad.getKey());
                startActivity(intent);
            }
        });


        if (Auth.getCurrentUser().getFavoritesAds().isEmpty()){
            showWelcomeText();
        }else{
            getAds();
        }
    }

    private void getAds(){
        if (isFirstLaunch){
            binding.progressBar.setVisibility(View.VISIBLE);
            isFirstLaunch = false;
        }
        binding.imageFavoriteIcon.setVisibility(View.GONE);
        binding.tvWelcome.setVisibility(View.GONE);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.recyclerViewFavorites.setLayoutManager(manager);
        binding.recyclerViewFavorites.setAdapter(adapter);

        DataBaseHelper.getFavoritesAd(new OnDataAdsReceivedListener<Ad>() {
            @Override
            public void onAdsReceived(ArrayList<Ad> resultArr) {
                ads.clear();
                for (int i = resultArr.size() - 1; i >= 0; i--) {
                    ads.add(resultArr.get(i));
                }

                Log.d("TAG", "onAdsReceived: " + ads.size());
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewFavorites.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }


    //если список избранных пуст, то показать кликабельный текст
    private void showWelcomeText(){
        binding.recyclerViewFavorites.setVisibility(View.INVISIBLE);
        binding.imageFavoriteIcon.setVisibility(View.VISIBLE);
        binding.tvWelcome.setVisibility(View.VISIBLE);
        SpannableString string = new SpannableString(requireContext()
                .getString(R.string.bottom_fragment_favorite_welcome_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                BottomNavigationView bnv = getActivity().findViewById(R.id.bottom_nav_menu);
                bnv.setSelectedItemId(R.id.home);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()).commit();

            }

            @Override
            public void updateDrawState(TextPaint clickableStr) {
                clickableStr.setColor(requireContext().getResources().getColor(R.color.main_color));
            }
        };

        int start, end;
        if (Locale.getDefault().getLanguage().equals("ru")){
            start = 51;
            end = 70;
        }else{
            start = 32;
            end = 44;
        }
        string.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        binding.tvWelcome.setText(string);
        binding.tvWelcome.setMovementMethod(LinkMovementMethod.getInstance());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //дай бог чтоб не было утечки аминь
        userFavoritesAds.removeEventListener(listener);
    }
}