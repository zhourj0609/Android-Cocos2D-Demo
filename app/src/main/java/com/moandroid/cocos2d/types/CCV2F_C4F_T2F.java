package com.moandroid.cocos2d.types;

public class CCV2F_C4F_T2F {
	public CCVertex2F vertices;
	public CCColor4F colors;
	public CCTex2F texCoords;
	
	public CCV2F_C4F_T2F(CCVertex2F v, CCColor4F c, CCTex2F t){
		vertices = new CCVertex2F(v.x, v.y);
		colors = new CCColor4F(c.r, c.g, c.b, c.a);
		texCoords = new CCTex2F(t.x, t.y);
	}
	
	public String toString(){
		return "< vertices = " + vertices + ", colors = " + colors + ", texCoords = " + texCoords + " >";
	}
}
