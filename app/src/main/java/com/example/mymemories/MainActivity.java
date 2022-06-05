package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gradient color
        TextView textViewColecciones = (TextView) findViewById(R.id.idColeccionesMainActivity);
        TextPaint paint = textViewColecciones.getPaint();
        float width = paint.measureText("COLECCIONES");
        Shader textShader = new LinearGradient(0, 0, width, textViewColecciones.getTextSize(),
                new int[]{
                        Color.parseColor("#ffcc80"),
                        Color.parseColor("#f57c00")
                }, null, Shader.TileMode.CLAMP);
        textViewColecciones.getPaint().setShader(textShader);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Notificación channel", "Notificación channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FloatingActionButton buttonCreateNewMemory = (FloatingActionButton) findViewById(R.id.floatingCreateMemory);
        buttonCreateNewMemory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemories.class);
                Bundle bundle = new Bundle();
                Recuerdo recuerdo = new Recuerdo();
                recuerdo = null;
                bundle.putSerializable("RECUERDO_DETAILS", recuerdo);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });

        ImageView viajesCategoria = (ImageView)  findViewById(R.id.idImageViewViajes);
        viajesCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListadoRecuerdos.class);
                Bundle bundle = new Bundle();
                String cat = "Viajes";
                bundle.putSerializable("BUNDLE_CAT", cat);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ImageView cervezasCategoria = (ImageView)  findViewById(R.id.idImageViewCervezas);
        cervezasCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListadoRecuerdos.class);
                Bundle bundle = new Bundle();
                String cat = "Cervezas";
                bundle.putSerializable("BUNDLE_CAT", cat);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageView cramosCategoria = (ImageView)  findViewById(R.id.idImageViewRamos);
        cramosCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListadoRecuerdos.class);
                Bundle bundle = new Bundle();
                String cat = "Ramos";
                bundle.putSerializable("BUNDLE_CAT", cat);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageView conciertoCategoria = (ImageView)  findViewById(R.id.idImageViewConciertos);
        conciertoCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListadoRecuerdos.class);
                Bundle bundle = new Bundle();
                String cat = "Conciertos";
                bundle.putSerializable("BUNDLE_CAT", cat);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}