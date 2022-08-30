package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> collectionAL = new ArrayList<String>();
    AdapterCollection adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerviewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new AdapterCollection(getApplicationContext(), collectionAL);
        adapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);

        FloatingActionButton buttonCreateNewMemory = (FloatingActionButton) findViewById(R.id.floatingCreateMemory);

        // Obtenemos las categorias
        RecuerdoDBHelper db = new RecuerdoDBHelper(this);
        Cursor cursor_categories = db.getAllCategoriasFromDB();

        if(cursor_categories.getCount() == 0){
            buttonCreateNewMemory.setEnabled(false);
        }else{
            buttonCreateNewMemory.setEnabled(true);
        }

        while (cursor_categories.moveToNext()) {
            Boolean flag_categoria = false;
            String categoria = cursor_categories.getString(cursor_categories.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA));
            for(int i = 0; i < collectionAL.size(); i++)
                if(categoria.equals(collectionAL.get(i)))
                    flag_categoria = true;
            if (!flag_categoria)
                collectionAL.add(cursor_categories.getString(cursor_categories.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA)));
        }
        collectionAL.add("Nueva categoria");


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

        buttonCreateNewMemory = (FloatingActionButton) findViewById(R.id.floatingCreateMemory);
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
    }
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            String coleccionSeleccionada = adapter.getCollectionByPosition(viewHolder.getAdapterPosition());
            if(coleccionSeleccionada.equals("Nueva categoria")){
                Intent intent = new Intent(MainActivity.this, NewCollection.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, ListadoRecuerdos.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BUNDLE_CAT", coleccionSeleccionada);
                intent.putExtras(bundle);
                finish();
                startActivity(intent);
            }
        }
    };
}