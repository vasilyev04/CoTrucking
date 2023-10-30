package com.kiparisov.cotrucking.model;

import java.io.Serializable;

public class TransporterAd extends Ad implements Serializable {
    public static final int AD_CAR_TYPE = 0;
    public static final int AD_TRUCK_TYPE = 1;

    public static final int TRUCK_TYPE_AWNING = 0;
    public static final int TRUCK_TYPE_CLOSED = 1;
    public static final int TRUCK_TYPE_OPENED = 2;

    private int loadCapacity;
    private int adTransporterType; //car or truck
    private int truckType;
    public TransporterAd() {
    }

    public int getAdType() {
        return adTransporterType;
    }

    public void setAdType(int adType) {
        this.adTransporterType = adType;
    }

    public int getTruckType() {
        return truckType;
    }

    public void setTruckType(int truckType) {
        this.truckType = truckType;
    }


    public TransporterAd(String title, int adTransporterType, String description,
                         String phone, String userName, String startLocation, String endLocation,
                         int loadCapacity, long createTime) {

        super(title, description, phone, userName, startLocation, endLocation, createTime);
        this.loadCapacity = loadCapacity;
        this.adTransporterType = adTransporterType;
    }


    public TransporterAd(String title, int adTransporterType, String description,
                         String phone, String userName, String startLocation, String endLocation,
                         int loadCapacity, int truckType, long createTime) {

        this(title, adTransporterType, description, phone, userName, startLocation,
                endLocation, loadCapacity, createTime);

        this.truckType = truckType;
    }

    public int getLoadCapacity() {
        return loadCapacity;
    }
}
