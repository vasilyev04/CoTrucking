package com.kiparisov.cotrucking.model;

import java.io.Serializable;

public class CargoAd extends Ad implements Serializable {
    public static final int DIMENSIONS_SMALL = 0;
    public static final int DIMENSIONS_MEDIUM = 1;
    public static final int DIMENSIONS_LARGE = 3;
    private int volume;
    private int weight;
    private int dimensions;

    public CargoAd(){

    }
    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public CargoAd(String title, String description, int volume, int weight,
                   String startLocation, String endLocation,
                   String phone, String userName, long createTime) {
        super(title, description, phone, userName, startLocation, endLocation, createTime);
        this.volume = volume;
        this.weight = weight;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
