package com.example.heartapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "heart.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE records (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, age INTEGER, result TEXT, percent INTEGER, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS records");
        onCreate(db);
    }

    public void insertRecord(String name, int age, String result, int percent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("age", age);
        cv.put("result", result);
        cv.put("percent", percent);
        cv.put("date", new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(new Date()));
        db.insert("records", null, cv);
    }

    public ArrayList<String> getAllRecords() {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT name, age, result, percent, date FROM records ORDER BY id DESC", null);
        while (c.moveToNext()) {
            list.add(c.getString(0) + " (age " + c.getInt(1) + ")\n"
                    + c.getString(2) + " - " + c.getInt(3) + "%\n" + c.getString(4));
        }
        c.close();
        return list;
    }

    public void clearAll() {
        getWritableDatabase().delete("records", null, null);
    }
}
