package com.example.caroline.safehome;

/**
 * Created by Caroline on 11/23/2015.
 */
public interface AccelerometerListener {
    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);
}
