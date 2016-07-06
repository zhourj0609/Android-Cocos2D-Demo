package com.moandroid.cocos2d.events;

public class CCStandardTouchHandler extends CCTouchHandler {

	public CCStandardTouchHandler(CCTouchDelegate delegate, int priority) {
		super(delegate, priority);
	}
	
	public static CCStandardTouchHandler handler(
			CCStandardTouchDelegate delegate, int priority) {
		return new CCStandardTouchHandler(delegate, priority);
	}

}
