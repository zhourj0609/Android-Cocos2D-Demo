package com.moandroid.cocos2d.actions;


import android.util.Log;

public class CCFiniteTimeAction extends CCAction {

	public static final String LOG_TAG = CCFiniteTimeAction.class.getSimpleName();
	
	public static CCFiniteTimeAction action(float d){
		return new CCFiniteTimeAction(d);
	}
	
	protected CCFiniteTimeAction(float d){
		_duration = d;
	}
	
	protected float _duration;
    public float duration() {
        return _duration;
    }
    public void setDuration(float duration) {
        _duration = duration;
    }

	public CCFiniteTimeAction copy() {
		return new CCFiniteTimeAction(_duration);
	}

    public CCFiniteTimeAction reverse() {
        Log.w(LOG_TAG, "Override me reverse ");
        return null;
    }
    
    public void update(float t){
    	Log.w(LOG_TAG, "Override me update");
    }
}
