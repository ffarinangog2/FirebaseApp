package com.example.firebaseapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    GoogleMap mapa;
    Marker marcador;
    double lat = 0;
    double lng = 0;
    // UI
    boolean primeraVez=true;
    TextView txtLatitud, txtLongitud;
    // UBICACIÓN (CLASE SEPARADA)
    Ubicacion ubicacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // FIREBASE
        DatabaseReference latRef = database.getReference("coordenadas/latitud");
        DatabaseReference lngRef = database.getReference("coordenadas/longitud");
        // MISMA ESTRUCTURA QUE SENSORES
        latRef.addValueEventListener(setListenerLat(txtLatitud));
        lngRef.addValueEventListener(setListenerLng(txtLongitud));

        ubicacion = new Ubicacion(this);
        // PERMISOS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            ubicacion.iniciarGPS();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);


    }
    public ValueEventListener setListenerLat(TextView txt) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    lat = Double.parseDouble(snapshot.getValue().toString());
                    txt.setText("Latitud: " + lat);
                    actualizarMapa();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {txt.setText("Latitud:");
            }
        };
    }
    public ValueEventListener setListenerLng(TextView txt) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {lng = Double.parseDouble(snapshot.getValue().toString());
                    txt.setText("Longitud: " + lng);
                    actualizarMapa();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {txt.setText("Longitud:");
            }
        };
    }
    public void actualizarMapa() {
        if (mapa == null) return;



        LatLng posicion = new LatLng(lat, lng);
        if (marcador == null) {
            marcador = mapa.addMarker(new MarkerOptions().position(posicion).title("Ubicación actual"));

        }
        else {marcador.setPosition(posicion);}
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 20));
    }
    @Override
    protected void onPause() {
        super.onPause();
        ubicacion.detenerGPS();
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ubicacion.iniciarGPS(
                );
            }
        }
    }

}
