package com.moandroid.cocos2d.types;

public class CCQuad2F {
	public static final int size = 2 * 4;
	public CCVertex2F tl;
	public CCVertex2F tr;
	public CCVertex2F bl;
	public CCVertex2F br;
	
	public CCQuad2F(CCVertex2F _tl, CCVertex2F _tr, CCVertex2F _bl, CCVertex2F _br){
		tl = new CCVertex2F(_tl.x, _tl.y);
		tr = new CCVertex2F(_tr.x, _tr.y);
		bl = new CCVertex2F(_bl.x, _bl.y);
		br = new CCVertex2F(_br.x, _br.y);
	}
	
	public CCQuad2F(){
		tl = new CCVertex2F(0, 0);
		tr = new CCVertex2F(1, 0);
		bl = new CCVertex2F(0, 1);
		br = new CCVertex2F(1, 1);
	}
	
	public String toString(){
		return "< t1 = " + tl + ", tr = " + tr + ", bl = " + bl + ", br = " + br + " >";
	}
	
	public float[] getFloats(){
		return new float[]{
			tl.x, tl.y,
			tr.x, tr.y,
			bl.x, bl.y,
			br.x, br.y
		};
	}
}
