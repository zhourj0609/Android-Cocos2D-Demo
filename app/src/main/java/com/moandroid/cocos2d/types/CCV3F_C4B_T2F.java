package com.moandroid.cocos2d.types;

public class CCV3F_C4B_T2F {
	public CCVertex3F vertices;
	public CCColor4B colors;
	public CCTex2F texCoords;
	
	public CCV3F_C4B_T2F(CCVertex3F v, CCColor4B c, CCTex2F t){
		vertices = new CCVertex3F(v.x, v.y, v.z);
		colors = new CCColor4B(c.r, c.g, c.b, c.a);
		texCoords = new CCTex2F(t.x, t.y);
	}
	
	public String toString(){
		return "< vertices = " + vertices + ", colors = " + colors + ", texCoords = " + texCoords + " >";
	}	
}
