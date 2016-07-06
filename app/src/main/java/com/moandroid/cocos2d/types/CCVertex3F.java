package com.moandroid.cocos2d.types;

public class CCVertex3F {
	public float x;
	public float y;
	public float z;
	public CCVertex3F(float dx, float dy, float dz){
		x = dx;
		y = dy;
		z = dz;
	}
	
	public String toString(){
		return "< x = " + x + ", y = " + y +", z = " +  z +">";
	}
}
