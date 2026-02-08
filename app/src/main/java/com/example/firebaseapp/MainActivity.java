package com.example.firebaseapp;

import static kotlinx.coroutines.internal.Concurrent_commonKt.setValue;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        DatabaseReference HumedadRef = 	database.getReference("sensores/humedad");

        DatabaseReference presionRef = database.getReference("sensores/presion");

        DatabaseReference TemperaturaRef = 	database.getReference("sensores/temperatura");

        DatabaseReference VelocidadRef = 	database.getReference("sensores/velocidad");


        TextView txt_temperatura = findViewById(R.id.valor_Temperatura);
        TemperaturaRef.addValueEventListener(setListener(txt_temperatura, "°C"));


        TextView txt_humedad = findViewById(R.id.valor_Humedad);
        HumedadRef.addValueEventListener(setListener(txt_humedad, "%"));



        TextView txt_presion = findViewById(R.id.valor_Presion);
        presionRef.addValueEventListener(setListener(txt_presion, "Hpa"));

        TextView txt_velocidad = findViewById(R.id.valor_Velocidad);
        VelocidadRef.addValueEventListener(setListener(txt_velocidad, "km/h"));


    }

    public void clickBotonTemperatura(View view){

        TextView txt_temperatura_edit = findViewById(R.id.setvalor_Temperatura);
        DatabaseReference TemperaturaRef = 	database.getReference("sensores/temperatura");
        TemperaturaRef.setValue(Float.parseFloat(txt_temperatura_edit.getText().toString()));
    }
    public void clickBotonHumedad(View view){

        TextView txt_humedad_edit = findViewById(R.id.setvalor_Humedad);
        DatabaseReference HumedadRef = 	database.getReference("sensores/humedad");
        HumedadRef.setValue(Float.parseFloat(txt_humedad_edit.getText().toString()));
    }


    public ValueEventListener setListener(TextView txt, String UnidadMedida){

        return (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt.setText(snapshot.getValue().toString() + " " + UnidadMedida);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txt.setText("");
            }
        });

    }

    public void abrirMapa(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }




}
