package com.kneedleapp.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;


public class LocationTracker implements LocationListener {

    private Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location location; // location
    double latitude = 0; // latitude
    double longitude = 0; // longitude
    double cellLatitude;
    double cellLongitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected LocationManager locationManager;
    private String mcc, mnc;
    private int cid, lac;
    private int countLocationCall = 0;
    Utils.LocationFoundListener locationListener;

    public LocationTracker(Context context, Utils.LocationFoundListener listener) {
        locationListener = listener;
        this.context = context;
        locationFind();
    }


    private void locationFind() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("Location Provider:", "GPS Enabled : "+isGPSEnabled +"  Network Enabled : "+isNetworkEnabled);

            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);



                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            sendMessageToActivity(latitude, longitude, "1");
                            return;
                        }
                    }
                }
            }
            if (isNetworkEnabled) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {

                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        sendMessageToActivity(latitude, longitude, "1");
                        return;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (latitude == 0 && longitude == 0 && countLocationCall < 10) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countLocationCall++;
                    locationFind();
                }
            }, 1000);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    private void sendMessageToActivity(Double latitude, Double longitude, String msg) {
        Log.e("Kneedle", "Location: " + latitude + "   " + longitude);
        locationListener.onLocationFoundStatus(latitude, longitude, msg);
        locationManager.removeUpdates(this);
    }

}
