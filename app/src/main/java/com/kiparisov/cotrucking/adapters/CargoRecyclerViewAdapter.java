package com.kiparisov.cotrucking.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.CargoAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CargoRecyclerViewAdapter extends RecyclerView.Adapter<CargoRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CargoAd> cargoAds;
    private OnItemClickListener listener;



    public CargoRecyclerViewAdapter(ArrayList<CargoAd> cargoAds, OnItemClickListener l) {
        this.cargoAds = cargoAds;
        this.listener = l;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDimensions, tvVolume, tvWeight, tvStartLocation, tvEndLocation,
                tvDate;
        ImageView image;
        ImageButton btnAddToFavorite;

        public ViewHolder(View v) {
            super(v);

            tvTitle = v.findViewById(R.id.title);
            tvDimensions = v.findViewById(R.id.dimensions);
            tvVolume = v.findViewById(R.id.volume);
            tvWeight = v.findViewById(R.id.weight);
            tvStartLocation = v.findViewById(R.id.startLocation);
            tvEndLocation = v.findViewById(R.id.endLocation);
            image = v.findViewById(R.id.image);
            tvDate = v.findViewById(R.id.date);

            btnAddToFavorite = v.findViewById(R.id.btn_add_to_favorite);

        }

        void bind(CargoAd ad, OnItemClickListener l){
            itemView.setOnClickListener(view -> l.onItemClickListener(ad, getAdapterPosition()));
            btnAddToFavorite.setOnClickListener(view -> l.onClickFavoriteButton(ad, btnAddToFavorite, getAdapterPosition()));
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_cargo_rv_vertical, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CargoAd cargoAd = cargoAds.get(position);
        holder.bind(cargoAd, listener);
        long time = cargoAd.getCreateTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");


        if (!TextUtils.isEmpty(cargoAd.getImageUri())){
            Glide.with(holder.itemView.getContext())
                    .load(cargoAd.getImageUri())
                    .into(holder.image);
            Log.d(cargoAd.getKey(), true + "");
        }else{
            Log.d(cargoAd.getKey(), false + "");
            holder.image.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.no_image));
        }


        ArrayList<String> favoritesAds = Auth.getCurrentUser().getFavoritesAds();
        if (favoritesAds.contains(cargoAd.getKey())){
            cargoAd.setFavorite(true);
        }else{
            cargoAd.setFavorite(false);
        }

        if (cargoAd.isFavorite()){
            holder.btnAddToFavorite.setImageDrawable(
                    holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_small_24));
        }else {
            holder.btnAddToFavorite.setImageDrawable(
                    holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_border_small_24));
        }

        String cargoDimensions;
        Context context = holder.itemView.getContext();
        switch (cargoAd.getDimensions()){
            case CargoAd.DIMENSIONS_SMALL:
                cargoDimensions = context.getString(R.string.cargo_type_small);
                break;
            case CargoAd.DIMENSIONS_MEDIUM:
                cargoDimensions = context.getString(R.string.cargo_type_medium);
                break;
            default:
                cargoDimensions = context.getString(R.string.cargo_type_large);
        }
        holder.tvTitle.setText(cargoAd.getTitle());
        holder.tvDimensions.setText(holder.itemView.getContext().getString(R.string.show_ad_title_dimensions) + ": " + cargoDimensions);
        holder.tvVolume.setText(":    " + cargoAd.getVolume() + " " + holder.itemView.getContext().getString(R.string.volume));
        holder.tvWeight.setText(":    " + cargoAd.getWeight() + " " + holder.itemView.getContext().getString(R.string.weight));
        holder.tvStartLocation.setText(cargoAd.getStartLocation());
        holder.tvEndLocation.setText(cargoAd.getEndLocation());
        holder.tvDate.setText(format.format(time));
    }

    @Override
    public int getItemCount() {
        return cargoAds.size();
    }

}
