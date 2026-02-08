package com.example.firebaseapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;
public class Ubicacion {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    AppCompatActivity activity;
    public Ubicacion(AppCompatActivity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                double latGPS = locationResult.getLastLocation().getLatitude();
                double lngGPS = locationResult.getLastLocation().getLongitude();
                database.getReference("coordenadas/latitud").setValue(latGPS);
                database.getReference("coordenadas/longitud").setValue(lngGPS);
            }
        };
    }
    @SuppressLint("MissingPermission")
    public void iniciarGPS() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    public void detenerGPS() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
