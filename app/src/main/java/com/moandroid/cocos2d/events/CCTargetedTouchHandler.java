package com.moandroid.cocos2d.events;

public class CCTargetedTouchHandler extends CCTouchHandler {

	public CCTargetedTouchHandler(CCTouchDelegate delegate, int priority) {
		super(delegate, priority);
	}

	public static CCTargetedTouchHandler handler(
			CCTargetedTouchDelegate delegate, int priority) {
		return new CCTargetedTouchHandler(delegate, priority);
	}

}
