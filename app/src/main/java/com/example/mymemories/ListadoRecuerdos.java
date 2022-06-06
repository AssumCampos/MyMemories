package com.example.mymemories;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListadoRecuerdos extends AppCompatActivity {
    ArrayList recuerdosViajes = new ArrayList<Recuerdo>();
    AdapterRecuerdo adapterRecuerdo;
    SearchView busquedaRecuerdos;
    String selectedSort;
    int selectedItem = 0;
    Intent details_intent;
    Bundle bundle;
    String categoria_bundle;
    RecuerdoDBHelper db;
    Cursor cursor_viajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_recuerdos);

        // Intent
        details_intent = getIntent();
        bundle = details_intent.getExtras();
        categoria_bundle = (String) bundle.getSerializable("BUNDLE_CAT");

        db = new RecuerdoDBHelper(this);
        cursor_viajes = db.findRecuerdosByCategoria(categoria_bundle);

        while (cursor_viajes.moveToNext()) {
            if(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_TITULO)) == null)
                continue;
            Recuerdo recuerdo = new Recuerdo();
            recuerdo.setId(cursor_viajes.getInt(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry._ID)));
            recuerdo.setCategoria(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA)));
            recuerdo.setTitulo(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_TITULO)));
            recuerdo.setFecha(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA)));
            recuerdo.setLugar(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_LUGAR)));
            recuerdo.setDescripcion(cursor_viajes.getString(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_DESCRIPCION)));

            byte[] imageInBlob = cursor_viajes.getBlob(cursor_viajes.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_IMAGEN));
            Bitmap bmp = BitmapFactory.decodeByteArray(imageInBlob, 0, imageInBlob.length);
            recuerdo.setImage(bmp);
            recuerdosViajes.add(recuerdo);
        }

        // Título
        TextView tituloCategoria = (TextView) findViewById(R.id.idCategoriaTitulo);
        tituloCategoria.setText(categoria_bundle);

        TextPaint paint = tituloCategoria.getPaint();
        float width = paint.measureText("categoria");
        Shader textShader = new LinearGradient(0, 0, width, tituloCategoria.getTextSize(),
                new int[]{
                        Color.parseColor("#ffcc80"),
                        Color.parseColor("#f57c00")
                }, null, Shader.TileMode.CLAMP);
        tituloCategoria.getPaint().setShader(textShader);

        // Recuerdos
        RecyclerView lv = findViewById(R.id.idRecyclerView);
        lv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterRecuerdo = new AdapterRecuerdo(getApplicationContext(), recuerdosViajes);
        adapterRecuerdo.setOnItemClickListener(onItemClickListener);
        lv.setAdapter(adapterRecuerdo);

        // SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        busquedaRecuerdos = (SearchView) findViewById(R.id.idSearchView);
        busquedaRecuerdos.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        busquedaRecuerdos.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        busquedaRecuerdos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapterRecuerdo.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapterRecuerdo.getFilter().filter(query);
                return false;
            }
        });

        busquedaRecuerdos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busquedaRecuerdos.setIconified(false);
            }
        });

        // Floating button add new memory
        FloatingActionButton buttonCreateNewMemoryByCategory = (FloatingActionButton) findViewById(R.id.floatingCreateMemoryByCategory);
        buttonCreateNewMemoryByCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListadoRecuerdos.this, AddMemories.class);
                Bundle bundle = new Bundle();
                Recuerdo recuerdo = new Recuerdo();
                recuerdo.setCategoria(categoria_bundle);
                bundle.putSerializable("RECUERDO_DETAILS", recuerdo);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });

        // Floating Button sort
        FloatingActionButton fab = findViewById(R.id.idButtonSort);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] order = {"A-Z", "Z-A", "Mas reciente", "Mas antiguo"};
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoRecuerdos.this);

                // set title
                builder.setTitle("Ordenar");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setSingleChoiceItems(order, selectedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedSort = order[i];
                        selectedItem = i;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(selectedItem)
                        {
                            case 0:
                                SortByTituloAsc();
                                fab.setImageResource(R.mipmap.a_z_icon);
                                break;
                            case 1:
                                SortByTituloDesc();
                                fab.setImageResource(R.mipmap.z_a_icon);
                                break;
                            case 2:
                                SortByFechaAsc();
                                fab.setImageResource(R.mipmap.calendar_asc);
                                break;
                            case 3:
                                SortByFechaDesc();
                                fab.setImageResource(R.mipmap.calendar_desc);
                                break;
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                // show dialog
                builder.show();
            }
        });
        // Sort A-Z
        SortByTituloAsc();
}

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(ListadoRecuerdos.this, RecuerdoDetails.class);

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            Recuerdo recuerdoSeleccionado = adapterRecuerdo.getRecuerdoByPosition(viewHolder.getAdapterPosition());

            Bundle bundle = new Bundle();

            Bitmap bitmap_imagenSeleccionada = recuerdoSeleccionado.getImage();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap_imagenSeleccionada.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            bundle.putSerializable("IMAGEN_SELECCIONADA", bs.toByteArray());

            recuerdoSeleccionado.setImage(null);
            bundle.putSerializable("RECUERDO_SELECCIONADO", recuerdoSeleccionado);
            myIntent.putExtras(bundle);

            finish();
            startActivity(myIntent);
        }
    };

    private void SortByTituloAsc(){
        Collections.sort(recuerdosViajes, new Comparator<Recuerdo>() {
            @Override
            public int compare(Recuerdo r1, Recuerdo r2) {
                Collator collator = Collator.getInstance();
                String r1Titulo = r1.getTitulo().toLowerCase();
                String r2Titulo = r2.getTitulo().toLowerCase();
                return collator.compare(r1Titulo, r2Titulo);
            }
        });

        adapterRecuerdo.notifyDataSetChanged();
    }

    private void SortByTituloDesc(){
        Collections.sort(recuerdosViajes, new Comparator<Recuerdo>() {
            @Override
            public int compare(Recuerdo r1, Recuerdo r2) {
                Collator collator = Collator.getInstance();
                String r1Titulo = r1.getTitulo().toLowerCase();
                String r2Titulo = r2.getTitulo().toLowerCase();
                return collator.compare(r2Titulo, r1Titulo);
            }
        });

        adapterRecuerdo.notifyDataSetChanged();
    }

   private void SortByFechaAsc(){
        Collections.sort(recuerdosViajes, new Comparator<Recuerdo>() {
            @Override
            public int compare(Recuerdo r1, Recuerdo r2) {
                SimpleDateFormat format = new SimpleDateFormat("dd / MM / yyyy");
                Date r1Date = null;
                try {
                    r1Date = format.parse(r1.getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date r2Date = null;
                try {
                    r2Date = format.parse(r2.getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return r2Date.compareTo(r1Date);
            }
        });

        adapterRecuerdo.notifyDataSetChanged();
    }

    private void SortByFechaDesc(){
        Collections.sort(recuerdosViajes, new Comparator<Recuerdo>() {
            @Override
            public int compare(Recuerdo r1, Recuerdo r2) {
                SimpleDateFormat format = new SimpleDateFormat("dd / MM / yyyy");
                Date r1Date = null;
                try {
                    r1Date = format.parse(r1.getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date r2Date = null;
                try {
                    r2Date = format.parse(r2.getFecha());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return r1Date.compareTo(r2Date);
            }
        });

        adapterRecuerdo.notifyDataSetChanged();
    }
}