package com.moandroid.cocos2d.types;

public class CCColor4F {
	public float r;
	public float g;
	public float b;
	public float a;
	
	public CCColor4F(float _r, float _g, float _b, float _a){
		r = _r;
		g = _g;
		b = _b;
	}
	
	public String toString(){
		return "< r = " + r + ", g = " + g +", b = " +  b + ", a = " + a + " >";
	}	
}
