package com.kiparisov.cotrucking.adapters;


import android.graphics.Bitmap;
import android.text.TextUtils;
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
import com.kiparisov.cotrucking.interfaces.OnAvatarClickListener;
import com.kiparisov.cotrucking.interfaces.OnItemClickListener;
import com.kiparisov.cotrucking.model.Ad;
import com.kiparisov.cotrucking.model.Auth;
import com.kiparisov.cotrucking.model.TransporterAd;
import com.kiparisov.cotrucking.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TransportersRecyclerViewAdapter extends RecyclerView.Adapter<TransportersRecyclerViewAdapter.ViewHolder> {
    private ArrayList<TransporterAd> ads;
    private OnItemClickListener listener;

    public TransportersRecyclerViewAdapter(ArrayList<TransporterAd> ads, OnItemClickListener l) {
        this.ads = ads;
        this.listener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvLoadCapacity, tvTruckType, tvStartLocation, tvEndLocation, tvDate;
        ImageView image, iconType;
        ImageButton btnAddToFavorite;

        ViewHolder(View v){
            super(v);

            image = v.findViewById(R.id.image);
            iconType = v.findViewById(R.id.icon_type);
            btnAddToFavorite = v.findViewById(R.id.btn_add_to_favorite);
            tvTitle = v.findViewById(R.id.title);
            tvTruckType = v.findViewById(R.id.truck_type);
            tvLoadCapacity = v.findViewById(R.id.loadCapacity);
            tvStartLocation = v.findViewById(R.id.startLocation);
            tvEndLocation = v.findViewById(R.id.endLocation);
            tvDate = v.findViewById(R.id.date);
        }

        void bind(Ad ad, OnItemClickListener l){
            btnAddToFavorite.setOnClickListener(view ->
                    l.onClickFavoriteButton(ad, btnAddToFavorite, getAdapterPosition()));

            itemView.setOnClickListener(view -> l.onItemClickListener(ad, getAdapterPosition()));
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_transporter_rv_vertical, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransporterAd ad = ads.get(position);
        holder.bind(ad, listener);
        long time = ad.getCreateTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");

        if (!TextUtils.isEmpty(ad.getImageUri())){
            Glide.with(holder.itemView.getContext())
                    .load(ad.getImageUri())
                    .into(holder.image);
        }else{
            holder.image.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.no_image));
        }

        ArrayList<String> favoritesAds = Auth.getCurrentUser().getFavoritesAds();
        if (favoritesAds.contains(ad.getKey())){
            ad.setFavorite(true);
        }else{
            ad.setFavorite(false);
        }


        if (ad.isFavorite()){
           holder.btnAddToFavorite.setImageDrawable(
                   holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_small_24));
        }else{
            holder.btnAddToFavorite.setImageDrawable(
                    holder.itemView.getContext().getDrawable(R.drawable.ic_baseline_favorite_border_small_24));
        }

        if (ad.getAdType() == TransporterAd.AD_TRUCK_TYPE){
            holder.tvTruckType.setVisibility(View.VISIBLE);

            switch (ad.getTruckType()){
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

        holder.tvTitle.setText(ad.getTitle());
        holder.tvLoadCapacity.setText(":    " + ad.getLoadCapacity() + " " +holder.itemView.getContext().getString(R.string.weight));
        holder.tvStartLocation.setText(ad.getStartLocation());
        holder.tvEndLocation.setText(ad.getEndLocation());
        holder.tvDate.setText(format.format(time));
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

}
