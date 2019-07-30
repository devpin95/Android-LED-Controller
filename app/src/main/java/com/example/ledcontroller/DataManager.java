package com.example.ledcontroller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Local database manager
 */
public class DataManager {

    private int version = 4;

    private SQLiteDatabase db;
    private String favoriteColorsTable = "favoriteColors";
    private String presetColorsTable = "presetColors";

    public DataManager(Context context) {
        SQLHelper helper = new SQLHelper(context);
        db = helper.getWritableDatabase();
    }

    public Cursor getFavoriteColors() {
        String query =
                "SELECT * " +
                "FROM " + favoriteColorsTable;
        return db.rawQuery(query, null);
    }

    public Cursor getPresetColors() {
        String query =
                "SELECT * " +
                        "FROM " + presetColorsTable;
        return db.rawQuery(query, null);
    }

    public Boolean insertColor(int color) {
        String query = "REPLACE INTO " + favoriteColorsTable + " (color) " + " VALUES (" + color + ")";
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            Log.i("info", "ERROR: " + e.toString());
            return false;
        }
    }

    public Boolean deleteColor(int color) {
        String query = "DELETE FROM " + favoriteColorsTable  + " WHERE color=" + color;
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            Log.i("info", "ERROR: " + e.toString());
            return false;
        }
    }

    private class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context context) {
            super(context, "color_sets", null, version);
        }

        public void onCreate(SQLiteDatabase db) {
            String query =
                    "CREATE TABLE " + presetColorsTable + "(" +
                            "color integer unique," +
                            "brightness integer)";
            db.execSQL(query);

            query =
                    "CREATE TABLE " + favoriteColorsTable + "(" +
                            "color integer unique," +
                            "brightness integer)";
            db.execSQL(query);

            int red = 0xFFFF0000;
            int green = 0xFF00FF00;
            int blue = 0xFF0000FF;
            int yellow = 0xFFFFFF00;
            int orange = 0XFFFF8800;
            int purple = 0xFF720f31;
            int blue2 = 0xFF4842DC;
            int pink = 0xFFF94A8F;
            int greenish = 0xFF148675;
            int darkred = 0xFF4f0a21;
            int pink2 = 0xFFe53978;
            int brightblue = 0xFF52c3f1;
            int green2 = 0xFF25a465;
            int pink3 = 0xFFdd44f9;
            int yellow2 = 0xFFac8820;
            int blue3 = 0xFF0982cf;

            query = "INSERT INTO " + presetColorsTable + " (color, brightness) " +
                    "values (" + red + "," + 100 +")," +
                    "(" + green  + ", " + 100 + ")," +
                    "(" + blue  + ", " + 100 + ")," +
                    "(" + yellow  + ", " + 100 + ")," +
                    "(" + purple  + ", " + 100 + ")," +
                    "(" + blue2  + ", " + 100 + ")," +
                    "(" + pink  + ", " + 100 + ")," +
                    "(" + greenish  + ", " + 100 + ")," +
                    "(" + darkred  + ", " + 100 + ")," +
                    "(" + pink2  + ", " + 100 + ")," +
                    "(" + brightblue  + ", " + 100 + ")," +
                    "(" + green2  + ", " + 100 + ")," +
                    "(" + pink3  + ", " + 100 + ")," +
                    "(" + yellow2  + ", " + 100 + ")," +
                    "(" + blue3  + ", " + 100 + ")," +
                    "(" + orange  + ", " + 100 + ")";
            db.execSQL(query);
        }

        public void onUpgrade(SQLiteDatabase db, int a, int b){
            db.execSQL( "DROP TABLE IF EXISTS " + presetColorsTable );
            db.execSQL( "DROP TABLE IF EXISTS " + favoriteColorsTable );
            onCreate(db);
        }
    }
}
