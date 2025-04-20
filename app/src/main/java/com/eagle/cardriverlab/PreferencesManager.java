package com.eagle.cardriverlab;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "CarDriverPreferences";
    private static final String KEY_CAR_NAME = "car_name";
    private static final String KEY_MAX_SPEED = "max_speed";
    private static final String KEY_ACCELERATION = "acceleration";
    private static final String KEY_DECELERATION = "deceleration";



    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveCarData(String carName, int maxSpeed, int acceleration, int deceleration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CAR_NAME, carName);
        editor.putInt(KEY_MAX_SPEED, maxSpeed);
        editor.putInt(KEY_ACCELERATION, acceleration);
        editor.putInt(KEY_DECELERATION, deceleration);



        editor.apply();
    }

    public String getCarName() {
        return sharedPreferences.getString(KEY_CAR_NAME, "Select A Car");
    }

    public int getMaxSpeed() {
        return sharedPreferences.getInt(KEY_MAX_SPEED, 0);
    }

    public int getAcceleration() {
        return sharedPreferences.getInt(KEY_ACCELERATION, 0);
    }

    public int getDeceleration() {
        return sharedPreferences.getInt(KEY_DECELERATION, 0);
    }




}


