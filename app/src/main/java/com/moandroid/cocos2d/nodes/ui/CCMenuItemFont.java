package com.moandroid.cocos2d.nodes.ui;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCMenuItemFont extends CCMenuItemLabel {
	
	public static void setFontSize(int s) {
	    _fontSize = s;
	}
	
	public static int fontSize() {
	    return _fontSize;
	}
	
	public static void setFontName(String n) {
	    _fontName = n;
	}
	
	public static String fontName() {
	    return _fontName;
	}
	
	public static CCMenuItemFont item(String value) {
	    return new CCMenuItemFont(CCLabel.label(value, _fontName, _fontSize), null, null);
	}
	
	public static CCMenuItemFont item(String value, CCNode rec, String cb) {
		CCLabel lbl = CCLabel.label(value, _fontName, _fontSize);
	    return new CCMenuItemFont(lbl, rec, cb);
	}
	
	protected CCMenuItemFont(CCLabel label, CCNode rec, String cb) {
	    super(label, rec, cb);
	}

}
