package com.yao.privacytest;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter implements Adapter {
    //final static String TAG = "LocationAdapter";
    private static Context context = null;

    //获取定位服务
    private static LocationManager locationManager;

    LocationAdapter (Context context) {
        LocationAdapter.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void finalize() throws Exception {
        LocationAdapter.context = null;
        if (locationManager != null) {
            locationManager.removeUpdates(locListener);
            locationManager = null;
        }
    }

    public static String[] startLocate() {
        List<String> result = new ArrayList<String>();

        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            result.add("Can not get location. Please open GPS.");
            return result.toArray(new String[result.size()]);
        }

        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        registerListener();

        List<String> list = locationManager.getProviders(true);

        Location bestLocation = null;
        String bestProvider = null;
        //Log.i(TAG, "==Get " + list.size() + " location providers==");

        for (String s : list) {
            Location location = locationManager.getLastKnownLocation(s); // 通过GPS获取位置

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();


                //筛选出最精准的传感器
                if (bestLocation == null) {
                    bestLocation = location;
                    bestProvider = s;
                    continue;
                }

                if (Float.valueOf(location.getAccuracy()).compareTo(bestLocation.getAccuracy()) >= 0) {
                    bestLocation = location;
                    bestProvider = s;
                }
            }
        }

        if (bestLocation == null || bestProvider == null) {
            result.add("Can not get location.");
            return result.toArray(new String[result.size()]);
        }

        //获取当前位置，这里只用到了经纬度
        result.add("纬度为：" + bestLocation.getLatitude());
        result.add("经度为：" + bestLocation.getLongitude());
        result.add("provider " + bestProvider);
        return result.toArray(new String[result.size()]);
    }

    private static LocationListener locListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            //Log.e(TAG, "===== [LocationListener.onStatusChanged] provider " + provider + " status " + status + " extras " + extras.toString() + " =====");
        }
        @Override
        public void onProviderEnabled(String provider) {
            //Log.e(TAG, "===== [LocationListener.onProviderEnabled] provider " + provider + " =====");
        }
        @Override
        public void onProviderDisabled(String provider) {
            //Log.e(TAG, "===== [LocationListener.onProviderDisabled] provider " + provider + " =====");
        }
        @Override
        public void onLocationChanged(Location location) {
            //Log.e(TAG, "===== [LocationListener.onLocationChanged] location altitude " + location.getAltitude() + " latitude " + location.getLatitude() + " longitude " + location.getLongitude() + " =====");
        }
    };

    private static void registerListener() {
        if (locationManager == null)
            return;
        locationManager.removeUpdates(locListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locListener);
    }

    public static String[] LocateDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to get location");
        return error.toArray(new String[error.size()]);
    }
}
