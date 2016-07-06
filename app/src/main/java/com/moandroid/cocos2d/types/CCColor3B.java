package com.moandroid.cocos2d.types;

public class CCColor3B {
	public byte r;
	public byte g;
	public byte b;
	
	public CCColor3B(byte _r, byte _g, byte _b){
		r = _r;
		g = _g;
		b = _b;
	}
	
	public String toString(){
		return "< r = " + (r & 0xFF) + ", g = " + (g & 0xFF) +", b = " + (b& 0xFF)+" >";
	}
}
