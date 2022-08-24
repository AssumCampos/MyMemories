package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddMemories extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private ImageView ivImage;
    String selectedDate;
    String category_text;
    boolean recuerdo_update;
    ArrayList<String> categories = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memories);
        recuerdo_update = true;

        // Notification
        createNotificationChannel();

        // Obtenemos el recuerdo si viene desde la pantalla de RecuerdoDetails
        Intent details_intent = getIntent();
        Bundle bundle = details_intent.getExtras();
        Recuerdo recuerdo = (Recuerdo) bundle.getSerializable("RECUERDO_DETAILS");

        // Photo
        ivImage = (ImageView) findViewById(R.id.ivImage);
        Button btnSelectPhoto = (Button) findViewById(R.id.idBtnSelectPhoto);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        // Date
        EditText etPlannedDate = (EditText) findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.etPlannedDate:
                        showDatePickerDialog();
                        break;
                }
            }

            private void showDatePickerDialog() {
                DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because January is zero
                        selectedDate = day + " / " + (month+1) + " / " + year;
                        etPlannedDate.setText(selectedDate);
                    }
                });
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Obtenemos las categorias
        RecuerdoDBHelper db = new RecuerdoDBHelper(this);
        Cursor cursor_categories = db.getAllCategoriasFromDB();

        while (cursor_categories.moveToNext()) {
            Boolean flag_categoria = false;
            String categoria = cursor_categories.getString(cursor_categories.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA));
            for(int i = 0; i < categories.size(); i++)
                if(categoria.equals(categories.get(i))){
                    flag_categoria = true;
                    break;
                }
            if (!flag_categoria)
                categories.add(cursor_categories.getString(cursor_categories.getColumnIndexOrThrow(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA)));

        }
        Spinner categoria = (Spinner) findViewById(R.id.idCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddMemories.this, android.R.layout.simple_spinner_item, categories);
        categoria.setAdapter(adapter);
        categoria.setSelection(1);
        // Listeners Spinner
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_text = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (recuerdo != null)
        {
            String categoria_rec = recuerdo.getCategoria();

            for (int i = 0; i < categories.size(); i++){
                if(categoria_rec.equals(categories.get(i))){
                    categoria.setSelection(i);
                }
            }
            String titulo_rec = recuerdo.getTitulo();
            recuerdo_update = false;
            if (titulo_rec != null){
                // Titulo
                TextView titulo_tv = (TextView) findViewById(R.id.idTitulo);
                titulo_tv.setText(titulo_rec);
                // Fecha
                etPlannedDate.setText(recuerdo.getFecha(), TextView.BufferType.EDITABLE);
                selectedDate = recuerdo.getFecha();

                // Image
                Bitmap bitmap = BitmapFactory.decodeByteArray(details_intent.getByteArrayExtra("IMAGEN_SELECCIONADA"), 0 , details_intent.getByteArrayExtra("IMAGEN_SELECCIONADA").length);
                ImageView imageView = (ImageView) findViewById(R.id.ivImage);
                imageView.setImageBitmap(bitmap);

                // Lugar
                TextView lugar_tv = (TextView) findViewById(R.id.idLugar);
                String lugar_rec = recuerdo.getLugar();
                lugar_tv.setText(lugar_rec);

                // Descripción
                TextView descripcion_tv = (TextView) findViewById(R.id.idDescripcion);
                String descripcion_rec = recuerdo.getDescripcion();
                descripcion_tv.setText(descripcion_rec);
                recuerdo_update = true;
            }
        }
        else{
            recuerdo_update = false;
        }
        // Añadir recuerdos
        Button addRecuerdo = findViewById(R.id.idButton);
        addRecuerdo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                if(recuerdo_update)
                {
                    Spinner idCategoria = (Spinner) findViewById(R.id.idCategory);
                    TextView idTitulo = (TextView) findViewById(R.id.idTitulo);
                    TextView idFecha = (TextView) findViewById(R.id.etPlannedDate);
                    TextView idLugar = (TextView) findViewById(R.id.idLugar);
                    TextView idDescripcion = (TextView) findViewById(R.id.idDescripcion);

                    // Imagen
                    ImageView idImage = (ImageView) findViewById(R.id.ivImage);
                    if (idImage.getDrawable() == null)
                        Toast.makeText(getApplicationContext(), "Debes rellenar la imagen", Toast.LENGTH_SHORT).show();
                        // Errors messages
                    else if (idTitulo.getText().toString().isEmpty() || idFecha.getText().toString().isEmpty() || idLugar.getText().toString().isEmpty()
                            || idDescripcion.getText().toString().isEmpty())
                        Toast.makeText(getApplicationContext(), "Debes rellenar todos los campos del recuerdo", Toast.LENGTH_SHORT).show();

                    else {
                        RecuerdoDBHelper dbHelper = new RecuerdoDBHelper(getApplicationContext());

                        // Formateamos las variables de la imagen
                        BitmapDrawable drawable = (BitmapDrawable) idImage.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();

                        // Creamos y guardamos esa información en un recuerdo
                        Recuerdo recuerdoUpdate = new Recuerdo();
                        recuerdoUpdate.setId(recuerdo.getId());
                        recuerdoUpdate.setCategoria(category_text);
                        recuerdoUpdate.setTitulo(idTitulo.getText().toString());
                        recuerdoUpdate.setFecha(idFecha.getText().toString());
                        recuerdoUpdate.setLugar(idLugar.getText().toString());
                        recuerdoUpdate.setDescripcion(idDescripcion.getText().toString());
                        recuerdoUpdate.setImage(bitmap);

                        dbHelper.updateRecuerdo(recuerdoUpdate);
                        Toast.makeText(getApplicationContext(), "Recuerdo modificado", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(i);
                    }
                }else
                    writeToDatabase(view);
            }
        });

    }
    public void writeToDatabase(View view)
    {
        // Base de datos
        RecuerdoDBHelper dbHelper = new RecuerdoDBHelper(this);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Cogemos la información del layout
        Spinner idCategoria = (Spinner) findViewById(R.id.idCategory);
        TextView idTitulo = (TextView) findViewById(R.id.idTitulo);
        TextView idFecha = (TextView) findViewById(R.id.etPlannedDate);
        TextView idLugar = (TextView) findViewById(R.id.idLugar);
        TextView idDescripcion = (TextView) findViewById(R.id.idDescripcion);
        // Imagen
        ImageView idImage = (ImageView) findViewById(R.id.ivImage);

        if (idImage.getDrawable() == null)
            Toast.makeText(getApplicationContext(), "Debes rellenar la imagen", Toast.LENGTH_SHORT).show();
        // Errors messages
        else if (idTitulo.getText().toString().isEmpty() || idFecha.getText().toString().isEmpty() || idLugar.getText().toString().isEmpty()
                || idDescripcion.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Debes rellenar todos los campos del recuerdo", Toast.LENGTH_SHORT).show();

        else{
            // Formateamos las variables de la imagen
            BitmapDrawable drawable = (BitmapDrawable) idImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            // Creamos y guardamos esa información en Recuerdo
            Recuerdo recuerdo = new Recuerdo();
            recuerdo.setCategoria(category_text);
            recuerdo.setTitulo(idTitulo.getText().toString());
            recuerdo.setFecha(idFecha.getText().toString());
            recuerdo.setLugar(idLugar.getText().toString());
            recuerdo.setDescripcion(idDescripcion.getText().toString());
            recuerdo.setImage(bitmap);

            // Image stuff
            ByteArrayOutputStream objectsByteArrayOutputStream = new ByteArrayOutputStream();
            byte[] imageInBytes;
            Bitmap imageToStoreBitmap = recuerdo.getImage();
            imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG, 90, objectsByteArrayOutputStream);
            imageInBytes = objectsByteArrayOutputStream.toByteArray();

            // Añadimos la información a values
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA, recuerdo.getCategoria());
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_TITULO, recuerdo.getTitulo());
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA, recuerdo.getFecha());
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_LUGAR, recuerdo.getLugar());
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_DESCRIPCION, recuerdo.getDescripcion());
            values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_IMAGEN, imageInBytes);

            // Añadimos a la base de datos
            long checkIfQueryRuns = db.insert(RecuerdoContract.RecuerdoEntry.TABLE_NAME, null, values);
            if (checkIfQueryRuns != 0){
                Toast.makeText(getApplicationContext(), "Recuerdo guardado", Toast.LENGTH_SHORT).show();
                setAlarm(recuerdo);
                Intent intent = new Intent(AddMemories.this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "No se ha podido guardar en BBDD", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Tomar foto"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Elegir una foto"))
                        galleryIntent();
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Tomar foto", "Elegir una foto", "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMemories.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddMemories.this);

                if (items[item].equals("Tomar foto")) {
                    userChoosenTask ="Tomar foto";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Elegir una foto")) {
                    userChoosenTask ="Elegir una foto";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }
    public void setAlarm(Recuerdo recuerdo)
    {
        Intent intent = new Intent(AddMemories.this, ShowNotification.class);
        intent.putExtra("categoria", recuerdo.getCategoria());
        intent.putExtra("titulo", recuerdo.getTitulo());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddMemories.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String[] day_month_year = selectedDate.split("/");
        Calendar myAlarmDate = new GregorianCalendar();
        myAlarmDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day_month_year[0].trim()));
        myAlarmDate.set(Calendar.MONTH, Integer.parseInt(day_month_year[1].trim())-1);
        myAlarmDate.set(Calendar.YEAR, Integer.parseInt(day_month_year[2].trim())+1);

        myAlarmDate.set(Calendar.HOUR_OF_DAY, 18);
        myAlarmDate.set(Calendar.MINUTE, 30);
        myAlarmDate.set(Calendar.SECOND, 00);

        long alarmTime = myAlarmDate.getTimeInMillis();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                alarmTime , DateUtils.YEAR_IN_MILLIS,
                pendingIntent);

    }
    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Assumtst";
            String description = "Desciprtion test";
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}