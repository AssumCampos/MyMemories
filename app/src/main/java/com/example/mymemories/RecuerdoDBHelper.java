package com.example.mymemories;

import static com.example.mymemories.RecuerdoContract.SQL_CREATE_ENTRIES;
import static com.example.mymemories.RecuerdoContract.SQL_DELETE_ENTRIES;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class RecuerdoDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION_VIAJES = 1;
    public static final String DATABASE_NAME_VIAJES = "RecuerdoDB.db";

    public RecuerdoDBHelper(Context context){
        super(context, DATABASE_NAME_VIAJES, null, DATABASE_VERSION_VIAJES);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor findRecuerdosByCategoria(String categoria)
    {
        RecuerdoDBHelper dbHelper = this;
        // Gets the data repository in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA + " = ?";
        String[] selectionArgs = { categoria };

        String sortOrder =
                RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA + " ASC";
        Cursor cursor = db.query(
                RecuerdoContract.RecuerdoEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        //db.close();
        return cursor;
    }
    public Cursor getAllCategoriasFromDB(){
        RecuerdoDBHelper dbHelper = this;
        // Gets the data repository in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String columns[] =  {RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA};
        String sortOrder =
                RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA + " ASC";
        Cursor cursor = db.query(
                RecuerdoContract.RecuerdoEntry.TABLE_NAME,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        //db.close();
        return cursor;
    }

    public Cursor findRecuerdosByCategoriaFechaDesc(String categoria)
    {
        RecuerdoDBHelper dbHelper = this;
        // Gets the data repository in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA + " = ?";
        String[] selectionArgs = { categoria };

        String sortOrder =
                RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA + " DESC";
        Cursor cursor = db.query(
                RecuerdoContract.RecuerdoEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        return cursor;
    }

    public void updateRecuerdo(Recuerdo recuerdo)
    {
        RecuerdoDBHelper dbHelper = this;
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Imagen
        ByteArrayOutputStream objectsByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] imageInBytes;
        Bitmap imageToStoreBitmap = recuerdo.getImage();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG, 90, objectsByteArrayOutputStream);

        imageInBytes = objectsByteArrayOutputStream.toByteArray();

        // Imagen categoria
        ByteArrayOutputStream categoria_objectsByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] categoria_imageInBytes;
        Bitmap categoria_imageToStoreBitmap = recuerdo.getImage_categoria();
        categoria_imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG, 90, categoria_objectsByteArrayOutputStream);

        categoria_imageInBytes = categoria_objectsByteArrayOutputStream.toByteArray();

        // Valores
        ContentValues values = new ContentValues();
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA, recuerdo.getCategoria());
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_TITULO, recuerdo.getTitulo());
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_FECHA, recuerdo.getFecha());
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_LUGAR, recuerdo.getLugar());
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_DESCRIPCION, recuerdo.getDescripcion());
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_CATEGORIA_IMAGEN, categoria_imageInBytes);
        values.put(RecuerdoContract.RecuerdoEntry.COLUMN_NAME_IMAGEN, imageInBytes);

        String selection = RecuerdoContract.RecuerdoEntry._ID + " = ?";

        String id = Integer.toString(recuerdo.getId());
        String[] selectionArgs = { id };

        db.update(
                RecuerdoContract.RecuerdoEntry.TABLE_NAME,
                values,
                selection, selectionArgs);
    }

    public void deleteRecuerdo(Recuerdo recuerdo)
    {
        RecuerdoDBHelper dbHelper = this;
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = RecuerdoContract.RecuerdoEntry._ID + " = ?";
        String id = Integer.toString(recuerdo.getId());
        String[] selectionArgs = { id };
        db.delete(RecuerdoContract.RecuerdoEntry.TABLE_NAME, selection, selectionArgs);
    }
}
