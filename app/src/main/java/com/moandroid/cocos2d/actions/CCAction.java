package com.moandroid.cocos2d.actions;

import android.util.Log;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCAction {
	public static final String LOG_TAG = CCAction.class.getSimpleName();
	
	public static final int INVALID_TAG = -1;
    public static CCAction action(){
    	return new CCAction();
    }
    
	private CCNode _originalTarget;
    public CCNode originalTarget() {
        return _originalTarget;
    }
    public void setOriginalTarget(CCNode node) {
    	_originalTarget = node;

    }
    
    protected CCNode _target;
    public CCNode target() {
        return _target;
    }
    public void setTarget(CCNode node) {
    	_target = node;
    }
    
	private int _tag = INVALID_TAG; 
	public int tag() {
		return _tag;
	} 
    public void setTag(int tag) {
    	_tag = tag;
    } 
    
    public void start(CCNode aTarget) {
        _originalTarget = _target = aTarget;
    }

    public void stop() {
        _target = null;
    }
    
    public CCAction copy(){
        CCAction copy = new CCAction();
        return copy;
    }
    
	public void step(float dt){
		 Log.w(LOG_TAG, "Override me");
	}
	
	public boolean isDone(){
		return true;
	}
	
    public void update(float time){
    	Log.w(LOG_TAG, "Override me");
    }
}
