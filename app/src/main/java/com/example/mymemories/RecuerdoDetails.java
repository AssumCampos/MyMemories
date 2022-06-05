package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RecuerdoDetails extends AppCompatActivity {
    Recuerdo recuerdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuerdo_details);

        Intent details_intent = getIntent();
        Bundle bundle = details_intent.getExtras();
        recuerdo = (Recuerdo) bundle.getSerializable("RECUERDO_SELECCIONADO");
        // Image
        Bitmap bitmap = BitmapFactory.decodeByteArray(details_intent.getByteArrayExtra("IMAGEN_SELECCIONADA"), 0 , details_intent.getByteArrayExtra("IMAGEN_SELECCIONADA").length);
        recuerdo.setImage(bitmap);
        ImageView imageView = (ImageView) findViewById(R.id.idImageViewListDetails);
        imageView.setImageBitmap(bitmap);

        // Titulo
        TextView titulo = (TextView) findViewById(R.id.idTituloListDetails);
        titulo.setText(recuerdo.getTitulo());

        // Fecha
        TextView fecha = (TextView) findViewById(R.id.idFechaListDetails);
        fecha.setText(recuerdo.getFecha());

        // Lugar
        TextView lugar = (TextView) findViewById(R.id.idLugarListDetails);
        lugar.setText(recuerdo.getLugar());

        // Descripción
        TextView descripcion = (TextView) findViewById(R.id.idDescripcionListDetails);
        descripcion.setText(recuerdo.getDescripcion());

        // Boton modifica el recuerdo
        Button buttonModificar =  (Button) findViewById(R.id.idButtonModificarDetails);
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecuerdoDetails.this, AddMemories.class);
                Bundle bundle = new Bundle();
                Bitmap bitmap_imagenSeleccionada = recuerdo.getImage();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap_imagenSeleccionada.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                bundle.putSerializable("IMAGEN_SELECCIONADA", bs.toByteArray());
                recuerdo.setImage(null);
                bundle.putSerializable("RECUERDO_DETAILS", recuerdo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // Boton elimina el recuerdo
        Button buttonEliminar =  (Button) findViewById(R.id.idButtonEliminaDetails);
        buttonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecuerdoDBHelper dbHelper = new RecuerdoDBHelper(getApplicationContext());
                dbHelper.deleteRecuerdo(recuerdo);
                Toast.makeText(getApplicationContext(), "Recuerdo eliminado", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        terminar();
    }

    public void terminar(){
        Intent intent = new Intent(RecuerdoDetails.this, ListadoRecuerdos.class);
        Bundle bundle = new Bundle();
        String cat = recuerdo.getCategoria();
        bundle.putSerializable("BUNDLE_CAT", cat);
        intent.putExtras(bundle);
        finish();
        startActivity(intent);

    }
}