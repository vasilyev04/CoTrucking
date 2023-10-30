package com.kiparisov.cotrucking.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

public abstract class Ad implements Serializable {
    private String title;
    private String description;
    private String phone;
    private String userName;
    private String startLocation;
    private String endLocation;
    private String imageUri;
    private String ownerKey;
    private String key;
    private long createTime;
    private boolean isFavorite;

    public Ad() {

    }

    public Ad(String title, String description, String phone, String userName, String startLocation, String endLocation,
              long createTime) {
        this.title = title;
        this.description = description;
        this.phone = phone;
        this.userName = userName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getOwnerKey() {
        return ownerKey;
    }


    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public static Bitmap convertBase64ToBitmap(String imageBase64){
        byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bitmap;
    }
}
