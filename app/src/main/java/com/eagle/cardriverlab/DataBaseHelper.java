package com.eagle.cardriverlab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {
        super(context, "carDriver", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Car (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "carName TEXT, " +
                "MaxSpeed INTEGER, " +
                "Acc INTEGER, " +
                "dec INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Car");
        onCreate(db);
    }

    // Method to insert a new car into the database
    public void insertCar(String carName, int maxSpeed, int acceleration, int deceleration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("carName", carName);
        values.put("MaxSpeed", maxSpeed);
        values.put("Acc", acceleration);
        values.put("dec", deceleration);

        db.insert("Car", null, values);
        db.close();
    }

    // Method to get all cars from the database
    public List<HashMap<String, String>> getAllCars() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HashMap<String, String>> carList = new ArrayList<>();
        String query = "SELECT * FROM Car";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> car = new HashMap<>();
                car.put("carName", cursor.getString(cursor.getColumnIndex("carName")));
                car.put("MaxSpeed", cursor.getString(cursor.getColumnIndex("MaxSpeed")));
                car.put("Acc", cursor.getString(cursor.getColumnIndex("Acc")));
                car.put("dec", cursor.getString(cursor.getColumnIndex("dec")));
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }
    // Method to delete a car from the database
    public void deleteCar(String carName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Car", "carName = ?", new String[]{carName});
        db.close();
    }
}
