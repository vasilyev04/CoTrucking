package com.kiparisov.cotrucking.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiparisov.cotrucking.interfaces.OnDataAdReceivedListener;
import com.kiparisov.cotrucking.interfaces.OnDataAdsReceivedListener;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {
    private static FirebaseDatabase db;

    public static void getCargoAds(OnDataAdsReceivedListener<CargoAd> listener){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        DatabaseReference cargoRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_CARGO_ADS_REFERENCE_KEY);
        ArrayList<CargoAd> resultArr = new ArrayList<>();

        cargoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CargoAd ad = ds.getValue(CargoAd.class);
                    resultArr.add(ad);
                }
                listener.onAdsReceived(resultArr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getTransporterAds(OnDataAdsReceivedListener<TransporterAd> listener){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        DatabaseReference transporterAdsRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_TRANSPORTER_ADS_REFERENCE_KEY);

        ArrayList<TransporterAd> resultArr = new ArrayList<>();
        transporterAdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TransporterAd ad = ds.getValue(TransporterAd.class);
                    /*
                     * проверка на наличие key объявления в
                     * коллекции избранных объявлений текущего юзера
                    */
                    resultArr.add(ad);
                }
               listener.onAdsReceived(resultArr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getFavoritesAd(OnDataAdsReceivedListener<Ad> listener){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);

        DatabaseReference tRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY).
                child(Constants.FB_TRANSPORTER_ADS_REFERENCE_KEY);
        DatabaseReference cRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_CARGO_ADS_REFERENCE_KEY);

        ArrayList<String> favoritesKeys = Auth.getCurrentUser().getFavoritesAds();
        ArrayList<Ad> resultArr = new ArrayList<>();

        for (String str: favoritesKeys) {
            tRef.orderByChild("key").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Ad ad = ds.getValue(TransporterAd.class);
                        ad.setFavorite(true);
                        resultArr.add(ad);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        for (String str : favoritesKeys) {
            cRef.orderByChild("key").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Ad ad = ds.getValue(CargoAd.class);
                        ad.setFavorite(true);
                        resultArr.add(ad);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        Task firstTask = tRef.get();
        Task secondTask = cRef.get();
        Task combinedTask = Tasks.whenAllSuccess(firstTask, secondTask).addOnSuccessListener(
                new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> list) {

                //сортировка в том порядке, в каком пользователь добавлял объявления к себе
                ArrayList<Ad> sortedResult = new ArrayList<>();
                for (int i = 0; i < favoritesKeys.size(); i++) {
                    for (int j = 0; j < resultArr.size(); j++) {
                        if (favoritesKeys.get(i).equals(resultArr.get(j).getKey())){
                            sortedResult.add(resultArr.get(j));
                            break;
                        }
                    }
                }
                listener.onAdsReceived(sortedResult);
            }
        });
    }

    public static void getUserAds(OnDataAdsReceivedListener<Ad> listener){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);

        DatabaseReference tRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY).
                child(Constants.FB_TRANSPORTER_ADS_REFERENCE_KEY);
        DatabaseReference cRef = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_CARGO_ADS_REFERENCE_KEY);

        ArrayList<String> userAds = Auth.getCurrentUser().getUserAds();
        ArrayList<Ad> resultArr = new ArrayList<>();

        for (String str: userAds) {
            tRef.orderByChild("key").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Ad ad = ds.getValue(TransporterAd.class);
                        ad.setFavorite(true);
                        resultArr.add(ad);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        for (String str : userAds) {
            cRef.orderByChild("key").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Ad ad = ds.getValue(CargoAd.class);
                        ad.setFavorite(true);
                        resultArr.add(ad);
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        Task firstTask = tRef.get();
        Task secondTask = cRef.get();
        Tasks.whenAllSuccess(firstTask, secondTask).addOnSuccessListener(
                new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> list) {
                        //сортировка в том порядке, в каком пользователь создавал объявления
                        ArrayList<Ad> sortedResult = new ArrayList<>();
                        for (int i = 0; i < userAds.size(); i++) {
                            for (int j = 0; j < resultArr.size(); j++) {
                                if (userAds.get(i).equals(resultArr.get(j).getKey())){
                                    sortedResult.add(resultArr.get(j));
                                    break;
                                }
                            }
                        }
                        listener.onAdsReceived(sortedResult);
                    }
                });
    }



    public static void getCargoAd(String key, OnDataAdReceivedListener l){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        DatabaseReference cargoAds = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_CARGO_ADS_REFERENCE_KEY);

        cargoAds.orderByChild("key").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    CargoAd ad = ds.getValue(CargoAd.class);
                    ArrayList<String> favoritesKey = Auth.getCurrentUser().getFavoritesAds();
                    if (favoritesKey.contains(ad.getKey())){
                        ad.setFavorite(true);
                    }else {
                        ad.setFavorite(false);
                    }
                    l.onAdReceived(ad);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getTransporterAd(String key, OnDataAdReceivedListener l){
        db = FirebaseDatabase.getInstance(Constants.FB_URL_CONNECTION);
        DatabaseReference transporterAds = db.getReference(Constants.FB_ADS_REFERENCE_KEY)
                .child(Constants.FB_TRANSPORTER_ADS_REFERENCE_KEY);

        transporterAds.orderByChild("key").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    TransporterAd ad = ds.getValue(TransporterAd.class);
                    ArrayList<String> favoritesKey = Auth.getCurrentUser().getFavoritesAds();
                    if (favoritesKey.contains(ad.getKey())){
                        ad.setFavorite(true);
                    }else {
                        ad.setFavorite(false);
                    }
                    l.onAdReceived(ad);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


