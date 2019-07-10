package com.example.ledcontroller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataManager {

    private SQLiteDatabase db;

    public DataManager(Context context) {
        SQLHelper helper = new SQLHelper(context);
        db = helper.getWritableDatabase();
    }

    public Cursor getFavoriteColors() {
        String query =
                "SELECT * " +
                "FROM favoriteColors";
        return db.rawQuery(query, null);
    }

    private class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context context) {
            super(context, "color_sets", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            String query =
                    "CREATE TABLE favoriteColors (" +
                            "color integer unique," +
                            "brightness integer)";
            db.execSQL(query);

            int red = 0xFFFF0000;
            int green = 0xFF00FF00;
            int blue = 0xFF0000FF;
            int yellow = 0xFFFFFF00;
            int orange = 0XFF8800;

            query = "INSERT INTO favoriteColors (color, brightness) " +
                    "values (" + red + "," + 100 +")," +
                    "(" + green  + ", " + 100 + ")," +
                    "(" + blue  + ", " + 100 + ")," +
                    "(" + yellow  + ", " + 100 + ")," +
                    "(" + orange  + ", " + 100 + ")";
            db.execSQL(query);
        }

        public void onUpgrade(SQLiteDatabase db, int a, int b){ /*do nothing*/ }
    }
}
