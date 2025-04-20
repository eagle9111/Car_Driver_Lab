package com.eagle.cardriverlab;

import android.graphics.Bitmap;

public class ForYouModel {
    private String carName, acceleration, deceleration, maxSpeed;
    private Bitmap imageBitmap;

    public ForYouModel(String carName, Bitmap imageBitmap, String acceleration, String deceleration, String maxSpeed) {
        this.carName = carName;
        this.imageBitmap = imageBitmap;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.maxSpeed = maxSpeed;
    }

    public String getCarName() { return carName; }
    public Bitmap getImageBitmap() { return imageBitmap; }
    public String getAcceleration() { return acceleration; }
    public String getDeceleration() { return deceleration; }
    public String getMaxSpeed() { return maxSpeed; }
}
