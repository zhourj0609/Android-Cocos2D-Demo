package com.moandroid.cocos2d.types;

public class CCPointSprite {
	public CCVertex2F pos;
	public CCColor4F colors;
	float size;
	
	public CCPointSprite(CCVertex2F p, CCColor4F c, float s){
		pos = new CCVertex2F(p.x, p.y);
		colors = new CCColor4F(c.r, c.g, c.b, c.a);
		size = s;
	}
	
	public String toString(){
		return "< pos = " + pos + ", colors = " + colors +", size = " + size +" >";
	}
}
