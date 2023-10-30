package com.kiparisov.cotrucking.interfaces;

import java.util.ArrayList;

public interface OnDataAdsReceivedListener<T> {
    void onAdsReceived(ArrayList<T> resultArr);
}
