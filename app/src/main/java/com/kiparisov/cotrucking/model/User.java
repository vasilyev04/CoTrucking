package com.kiparisov.cotrucking.model;

import java.util.ArrayList;

public class User {
    public static final int CLIENT_INDICATOR = 0;
    public static final int TRANSPORTER_INDICATOR = 1;
    public static final int ADMINISTRATION_INDICATOR = 2;
    private String email;
    private String passwd;
    private String name;
    private String phone;
    private String profileDescription;
    private String avatarUri;
    private int age;
    private int userIndicator;
    private String UID;
    private String key;
    private boolean isEmailPrivate = true;
    private boolean isPhonePrivate = true;
    private ArrayList<String> favoritesAds = new ArrayList<>();
    private ArrayList<String> userAds = new ArrayList<>();

    public User() {
    }

    public User(String email, String passwd, String firstName, String UID, String key) {
        this.email = email;
        this.passwd = passwd;
        this.name = firstName;
        this.UID = UID;
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getFavoritesAds() {
        return favoritesAds;
    }

    public void addFavoritesAds(String adKey) {
        this.favoritesAds.add(adKey);
    }

    public void setFavoritesAds(ArrayList<String> arrayList){
        this.favoritesAds = arrayList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getUserIndicator() {
        return userIndicator;
    }

    public void setUserIndicator(int userIndicator) {
        this.userIndicator = userIndicator;
    }

    public boolean isEmailPrivate() {
        return isEmailPrivate;
    }

    public void setEmailPrivate(boolean emailPrivate) {
        isEmailPrivate = emailPrivate;
    }

    public boolean isPhonePrivate() {
        return isPhonePrivate;
    }

    public void setPhonePrivate(boolean phonePrivate) {
        isPhonePrivate = phonePrivate;
    }

    public ArrayList<String> getUserAds() {
        return userAds;
    }

    public void setUserAds(ArrayList<String> userAds) {
        this.userAds = userAds;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}
