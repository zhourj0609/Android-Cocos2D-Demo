package com.moandroid.cocos2d.util;

public class CCProfiler {

	private static CCProfiler _sharedProfiler;
	public static CCProfiler sharedProfiler() {
		if(_sharedProfiler == null){
			_sharedProfiler = new CCProfiler();
		}
		return _sharedProfiler;
	}
	public void displayTimers() {
		// TODO Auto-generated method stub
		
	}

}
