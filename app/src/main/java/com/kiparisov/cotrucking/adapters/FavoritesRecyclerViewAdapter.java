package com.kiparisov.cotrucking.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.CargoAd;
import com.kiparisov.cotrucking.model.TransporterAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder> {
    private static final int TYPE_CARGO = 0;
    private static final int TYPE_TRANSPORTER = 1;
    private ArrayList<Ad> ads;
    private OnItemClickListener listener;

    public FavoritesRecyclerViewAdapter(ArrayList<Ad> ads, OnItemClickListener listener) {
        this.ads = ads;
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        Ad ad = ads.get(position);
        if (ad instanceof CargoAd){
            Log.d("TAG", "getItemViewType: " + "TYPE_CARGO");
            return TYPE_CARGO;
        }else{
            Log.d("TAG", "getItemViewType: " + "TYPE_TRANSPORTER");
            return TYPE_TRANSPORTER;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDimensions, tvVolume, tvWeight, tvStartLocation, tvEndLocation,
                tvLoadCapacity, tvTruckType, tvDate;
        ImageView image, iconType;
        ImageButton btnAddToFavorite;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.title);
            tvDimensions = itemView.findViewById(R.id.dimensions);
            tvVolume = itemView.findViewById(R.id.volume);
            tvWeight = itemView.findViewById(R.id.weight);
            tvStartLocation = itemView.findViewById(R.id.startLocation);
            tvEndLocation = itemView.findViewById(R.id.endLocation);
            image = itemView.findViewById(R.id.image);
            tvDate = itemView.findViewById(R.id.date);
            iconType = itemView.findViewById(R.id.icon_type);
            tvTruckType = itemView.findViewById(R.id.truck_type);
            tvLoadCapacity = itemView.findViewById(R.id.loadCapacity);
            btnAddToFavorite = itemView.findViewById(R.id.btn_add_to_favorite);

        }

        void bind(Ad ad, OnItemClickListener listener){
            btnAddToFavorite.setOnClickListener(view ->
                    listener.onClickFavoriteButton(ad, btnAddToFavorite, getAdapterPosition()));

            itemView.setOnClickListener(view -> {
                listener.onItemClickListener(ad, getAdapterPosition());
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType){
            case TYPE_CARGO:
                v = inflater.inflate(R.layout.item_cargo_rv_horizontal, parent, false);
                break;
            default:
                v = inflater.inflate(R.layout.item_transporter_rv_horizontal, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ad ad = ads.get(position);
        holder.bind(ad, listener);
        long time = ad.getCreateTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");

        if (!TextUtils.isEmpty(ad.getImageUri())){
            Glide.with(holder.itemView.getContext())
                    .load(ad.getImageUri())
                    .into(holder.image);
        }else {
            holder.image.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.no_image));
        }

        if (ad.isFavorite()){
            holder.btnAddToFavorite.setImageDrawable(
                    holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_small_24));
        }else {
            holder.btnAddToFavorite.setImageDrawable(
                    holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_border_small_24));
        }
        holder.tvStartLocation.setText(ad.getStartLocation());
        holder.tvEndLocation.setText(ad.getEndLocation());
        holder.tvDate.setText(format.format(time));

        switch (getItemViewType(position)){
            case TYPE_CARGO:
                CargoAd cargoAd = (CargoAd) ads.get(position);
                holder.tvTitle.setText(cargoAd.getTitle());
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
                holder.tvDimensions.setText(holder.itemView.getContext().getString(R.string.show_ad_title_dimensions) + ": " + cargoDimensions);
                holder.tvVolume.setText(":    " + cargoAd.getVolume() + " " + holder.itemView.getContext().getString(R.string.volume));
                holder.tvWeight.setText(":    " + cargoAd.getWeight() + " " + holder.itemView.getContext().getString(R.string.weight));
                break;
            default:
                TransporterAd transporterAd = (TransporterAd) ads.get(position);
                holder.tvTitle.setText(transporterAd.getTitle());
                holder.tvLoadCapacity.setText(":    " + transporterAd.getLoadCapacity() + holder.itemView.getContext().getString(R.string.weight));
                if (transporterAd.getAdType() == TransporterAd.AD_TRUCK_TYPE){
                    holder.tvTruckType.setVisibility(View.VISIBLE);
                    switch (transporterAd.getTruckType()){
                        case TransporterAd.TRUCK_TYPE_AWNING:
                            holder.tvTruckType.setText(holder
                                    .itemView
                                    .getContext()
                                    .getString(R.string.type) + " " +
                                    holder
                                            .itemView
                                            .getContext()
                                            .getString(R.string.truck_type_tent));
                            break;
                        case TransporterAd.TRUCK_TYPE_CLOSED:
                            holder.tvTruckType.setText(holder
                                    .itemView
                                    .getContext()
                                    .getString(R.string.type) + " " +
                                    holder
                                            .itemView
                                            .getContext()
                                            .getString(R.string.truck_type_closed));
                            break;
                        default:
                            holder.tvTruckType.setText(holder
                                    .itemView
                                    .getContext()
                                    .getString(R.string.type) + " " +
                                    holder
                                            .itemView
                                            .getContext()
                                            .getString(R.string.truck_type_open));
                    }
                    holder.iconType.setImageDrawable(
                            holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_fire_truck_24));
                }else{
                    holder.tvTruckType.setVisibility(View.GONE);
                    holder.iconType.setImageDrawable(
                            holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_directions_car_24));
                }
        }
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }
}
