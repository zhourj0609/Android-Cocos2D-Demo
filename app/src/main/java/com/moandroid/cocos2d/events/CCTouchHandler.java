package com.moandroid.cocos2d.events;

public class CCTouchHandler {

	private int _priority;
	public int priority() {
		return _priority;
	}
	
//	public void setPriority(int pri){
//		_priority = pri;
//	}

	private CCTouchDelegate _delegate;
	public CCTouchDelegate delegate() {
		return _delegate;
	}
	
	public static CCTouchHandler handler(CCTouchDelegate delegate, int priority){
		return new CCTouchHandler(delegate, priority);
	}
	
    public CCTouchHandler(CCTouchDelegate delegate, int priority){
        _delegate = delegate;
        _priority = priority;
    }
}
