
package com.quranreading.listeners;

public interface OnLocationSetListner {

    void onLocationSet(String cityName, double latitude, double longitude);

    void onNewLocationDetected(String newCityName, String oldCityName, double latitude, double longitude);
}
