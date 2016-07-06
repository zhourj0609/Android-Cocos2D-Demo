package com.moandroid.cocos2d.types;

public class CCTex2F {
	public float x;
	public float y;
	public CCTex2F(float _x, float _y){
		x = _x;
		y = _y;
	}
	
	public String toString(){
		return "< x = " + x + ", y = " + y + " >";
	}
}
