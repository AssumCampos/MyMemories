package com.example.mymemories;
import android.provider.BaseColumns;

public class RecuerdoContract {
    private RecuerdoContract(){}

    public static class RecuerdoEntry implements BaseColumns{
        public static final String TABLE_NAME = "recuerdoDB";
        public static final String COLUMN_NAME_TITULO = "recuerdoTitulo";
        public static final String COLUMN_NAME_FECHA = "recuerdoFecha";
        public static final String COLUMN_NAME_IMAGEN = "recuerdoImagen";
        public static final String COLUMN_NAME_LUGAR = "recuerdoLugar";
        public static final String COLUMN_NAME_DESCRIPCION = "recuerdoDescripcion";
        public static final String COLUMN_NAME_CATEGORIA = "recuerdoCategoria";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecuerdoEntry.TABLE_NAME + " (" +
                    RecuerdoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RecuerdoEntry.COLUMN_NAME_TITULO + " TEXT," +
                    RecuerdoEntry.COLUMN_NAME_FECHA + " TEXT, " +
                    RecuerdoEntry.COLUMN_NAME_LUGAR + " TEXT," +
                    RecuerdoEntry.COLUMN_NAME_DESCRIPCION + " TEXT," +
                    RecuerdoEntry.COLUMN_NAME_CATEGORIA + " TEXT," +
                    RecuerdoEntry.COLUMN_NAME_IMAGEN + " BLOB)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecuerdoEntry.TABLE_NAME;
}
