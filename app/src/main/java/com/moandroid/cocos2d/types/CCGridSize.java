package com.moandroid.cocos2d.types;

public class CCGridSize {
	public int x;
	public int y;
	
	public CCGridSize(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public String toString(){
		return "< x = " + x + ", y = " + y + " >";
	}
}
