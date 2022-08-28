package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

public class NewCollection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collection);


        Button buttonCreateNewMemory = (Button) findViewById(R.id.buttonNewCollection);
        buttonCreateNewMemory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText collection_name = (EditText) findViewById(R.id.idNameCategory);
                String categoria_str = collection_name.getText().toString();
                if (categoria_str.isEmpty()){
                    Toast.makeText(getApplicationContext(), "ERROR: Nombre de colección vacío", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Base de datos
                    RecuerdoDBHelper dbHelper = new RecuerdoDBHelper(getApplicationContext());
                    // Gets the data repository in write mode
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();


                    // Creamos y guardamos esa información en Recuerdo
                    Recuerdo recuerdo = new Recuerdo();

                    String categoria_capitalLetter = categoria_str.substring(0, 1).toUpperCase() + categoria_str.substring(1);
                    recuerdo.setCategoria(categoria_capitalLetter);
                    recuerdo.setTitulo(null);
                    recuerdo.setFecha(null);
                    recuerdo.setLugar(null);
                    recuerdo.setDescripcion(null);
                    recuerdo.setImage(null);


                    // Añadimos la información a values
                    values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA, recuerdo.getCategoria());
                    values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_TITULO, recuerdo.getTitulo());
                    values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA, recuerdo.getFecha());
                    values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_LUGAR, recuerdo.getLugar());
                    values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_DESCRIPCION, recuerdo.getDescripcion());
                    //values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_IMAGEN, imageInBytes);

                    long checkIfQueryRuns = db.insert(RecuerdoContract.RecuerdoEntry.TABLE_NAME, null, values);
                    Intent intent = new Intent(NewCollection.this, MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "Colección creada", Toast.LENGTH_SHORT).show();

                }

            }

        });
    }
}