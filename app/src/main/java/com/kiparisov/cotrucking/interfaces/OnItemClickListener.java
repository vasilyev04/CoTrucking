package com.kiparisov.cotrucking.interfaces;

import android.widget.ImageButton;

import com.kiparisov.cotrucking.model.Ad;

public interface OnItemClickListener {
    void onClickFavoriteButton(Ad ad, ImageButton favorite, int position);

    void onItemClickListener(Ad ad, int position);
}
