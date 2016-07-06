package com.moandroid.cocos2d.types;

public class CCQuad4B {
	public static final int size = 4 * 4;
	public CCColor4B tl;
	public CCColor4B tr;
	public CCColor4B bl;
	public CCColor4B br;
	public CCQuad4B(CCColor4B _tl, CCColor4B _tr, CCColor4B _bl, CCColor4B _br){
		tl = new CCColor4B(_tl.r, _tl.g, _tl.b, _tl.a);
		tr = new CCColor4B(_tr.r, _tr.g, _tr.b, _tr.a);
		bl = new CCColor4B(_bl.r, _bl.g, _bl.b, _bl.a);
		br = new CCColor4B(_br.r, _br.g, _br.b, _br.a);
	}
	
	public CCQuad4B(CCColor4B _clr){
		tl = new CCColor4B(_clr.r, _clr.g, _clr.b, _clr.a);
		tr = new CCColor4B(_clr.r, _clr.g, _clr.b, _clr.a);
		bl = new CCColor4B(_clr.r, _clr.g, _clr.b, _clr.a);
		br = new CCColor4B(_clr.r, _clr.g, _clr.b, _clr.a);
	}
	
	public String toString(){
		return "< t1 = " + tl + ", tr = " + tr + ", bl = " + bl + ", br = " + br + " >";
	}
	
	public byte[] getBytes(){
		return new byte[]{
			tl.r, tl.g, tl.b, tl.a,
			tr.r, tr.g, tr.b, tr.a,
			bl.r, bl.g, bl.b, bl.a,
			br.r, br.g, br.b, br.a
		};
	}
}
