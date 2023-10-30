package com.kiparisov.cotrucking.ui.vpfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.interfaces.OnDataAdsReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.Constants;
import com.kiparisov.cotrucking.databinding.FragmentCargoBinding;
import com.kiparisov.cotrucking.adapters.CargoRecyclerViewAdapter;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.DataBaseHelper;
import com.kiparisov.cotrucking.ui.ShowAdCargoActivity;

import java.util.ArrayList;


public class CargoFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference userFavoritesAds;
    private FragmentCargoBinding binding;
    private ArrayList<CargoAd> ads;
    private CargoRecyclerViewAdapter adapter;
    private ValueEventListener listener;

    private Snackbar snackbar;


    private void init(){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        SharedPreferences sp = requireContext().getSharedPreferences(Constants.SP_USER_SIGN_KEY,
                Context.MODE_PRIVATE);

        try {
            userFavoritesAds = db.getReference(Constants.FB_USERS_REFERENCE_KEY)
                    .child(sp.getString("KEY",null)).child("favoritesAds");
        }catch (Exception ignored){
            //если пользователоь вышел
        }


        //листенер на изменения в ветке избранных объявлений
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ads = new ArrayList<>();
        adapter = new CargoRecyclerViewAdapter(ads, new OnItemClickListener() {
            @Override
            public void onClickFavoriteButton(Ad ad, ImageButton favorite, int position) {
                if (ad.isFavorite()){
                    ad.setFavorite(false);
                    ArrayList<String> favoriteAds = Auth.getCurrentUser().getFavoritesAds();
                    favoriteAds.remove(ad.getKey());

                    favorite.setImageDrawable(
                            requireContext().getDrawable(R.drawable.ic_baseline_favorite_border_small_24));

                    ContextThemeWrapper ctw = new ContextThemeWrapper(requireContext(), R.style.CustomSnackbarTheme);
                    snackbar = Snackbar.make(ctw, getView(),
                            requireContext().getString(R.string.snackbar_removed_from_favorites),
                            Snackbar.LENGTH_SHORT);
                }else{
                    ad.setFavorite(true);
                    ArrayList<String> favoriteAds = Auth.getCurrentUser().getFavoritesAds();
                    favoriteAds.add(ad.getKey());

                    favorite.setImageDrawable(
                            requireContext().getDrawable(R.drawable.ic_baseline_favorite_small_24));

                    ContextThemeWrapper ctw = new ContextThemeWrapper(requireContext(), R.style.CustomSnackbarTheme);
                    snackbar = Snackbar.make(ctw, getView(),
                            requireContext().getString(R.string.snackbar_added_to_favorites),
                            Snackbar.LENGTH_SHORT);
                }
                snackbar.setAnchorView(R.id.bottom_nav_menu);
                snackbar.dismiss();
                snackbar.show();
                Auth.updateUserInFireBase(Auth.getCurrentUser());
            }

            @Override
            public void onItemClickListener(Ad ad, int position) {
                Intent intent = new Intent(requireContext(), ShowAdCargoActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_AD_KEY, ad.getKey());
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCargoBinding.inflate(getLayoutInflater());

        init();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL);
        binding.recyclerViewCargo.setLayoutManager(layoutManager);

        binding.recyclerViewCargo.setAdapter(adapter);

        getAds();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearRecyclerView();
                getAds();
            }
        });
    }


    private void getAds(){
        binding.swipeRefreshLayout.setRefreshing(true);
        DataBaseHelper.getCargoAds(new OnDataAdsReceivedListener<CargoAd>() {
            @Override
            public void onAdsReceived(ArrayList<CargoAd> resultArr) {
                for (int i = resultArr.size() - 1; i >= 0 ; i--) {
                    ads.add(resultArr.size() - 1 - i, resultArr.get(i));
                }
                binding.swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });


        try {
            userFavoritesAds.addValueEventListener(listener);
        }catch (Exception ignored){
            //если пользователь вышел
        }
    }


    private void clearRecyclerView(){
        int size = ads.size();
        ads.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //дай бог чтоб не было утечек аминь
        try {
            userFavoritesAds.removeEventListener(listener);
        }catch (Exception ignored){
            //если пользователоь вышел
        }
    }
}