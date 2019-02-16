package com.example.harry.sqltest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** DatabaseHelper.java
 * Creates a SQLite database to store user profile information
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "profiles.db";
    public static final String TABLE_NAME = "profiles_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "LIPASE";
    public static final String COL_4 = "TABLETS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, LIPASE INTEGER, TABLETS INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name, int lipase, int tablets){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, lipase);
        contentValues.put(COL_4, tablets);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }
        else
            return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COL_1+" FROM "+TABLE_NAME+" WHERE "+COL_2+" = '"+name+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemLipase(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COL_3+" FROM "+TABLE_NAME+" WHERE "+COL_2+" = '"+name+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemTablets(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COL_4+" FROM "+TABLE_NAME+" WHERE "+COL_2+" = '"+name+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
