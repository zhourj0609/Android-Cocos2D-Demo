package com.moandroid.cocos2d.types;

public class CCColor3F {
	public float r;
	public float g;
	public float b;
	
	public CCColor3F(float _r, float _g, float _b){
		r = _r;
		g = _g;
		b = _b;
	}
	
	public String toString(){
		return "< r = " + r + ", g = " + g +", b = " +  b +" >";
	}	
}
