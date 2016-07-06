package com.moandroid.cocos2d.types;

public class CCColor4B {
	public byte r;
	public byte g;
	public byte b;
	public byte a;

	public CCColor4B(byte _r, byte _g, byte _b, byte _a){
		r = _r;
		g = _g;
		b = _b;
		a = _a;
	}
	
	public String toString(){
		return "< r = " + (r & 0xFF) + ", g = " + (g & 0xFF) +", b = " + (b & 0xFF)+ ", a = "+ (a & 0xFF)+ " >";
	}
}
