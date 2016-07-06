package com.moandroid.cocos2d.types;

public class CCVertex2F {
	public float x;
	public float y;
	public CCVertex2F(float dx, float dy){
		x = dx;
		y = dy;
	}
	public String toString(){
		return "< x = " + x + ", y = " + y + ">";
	}
}
