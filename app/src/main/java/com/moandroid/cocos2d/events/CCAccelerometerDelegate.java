package com.moandroid.cocos2d.events;

public interface CCAccelerometerDelegate {
	boolean accelerometerAccuracyChanged(int accuracy);
	public boolean accelerometerChanged(float accelX, float accelY, float accelZ);
}