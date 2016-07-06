package com.moandroid.cocos2d.types;

public class CCQuad3F {
	public static final int size = 3 * 4;
	public CCVertex3F tl;
	public CCVertex3F tr;
	public CCVertex3F bl;
	public CCVertex3F br;
	
	public CCQuad3F(CCVertex3F _tl, CCVertex3F _tr, CCVertex3F _bl, CCVertex3F _br){
		tl = new CCVertex3F(_tl.x, _tl.y, _tl.z);
		tr = new CCVertex3F(_tr.x, _tr.y, _tr.z);
		bl = new CCVertex3F(_bl.x, _bl.y, _bl.z);
		br = new CCVertex3F(_br.x, _br.y, _br.z);
	}
	
	public CCQuad3F(){
		tl = new CCVertex3F(0, 0, 0);
		tr = new CCVertex3F(1, 0, 0);
		bl = new CCVertex3F(0, 1, 0);
		br = new CCVertex3F(1, 1, 0);
	}
	
	public String toString(){
		return "< t1 = " + tl + ", tr = " + tr + ", bl = " + bl + ", br = " + br + " >";
	}
	
	public float[] getFloats(){
		return new float[]{
			tl.x, tl.y, tl.z,
			tr.x, tr.y, tr.z,
			bl.x, bl.y, bl.z,
			br.x, br.y, br.z
		};
	}
}
