package com.moandroid.cocos2d.actions.interval;

import android.util.Log;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCIntervalAction extends CCFiniteTimeAction {
	
	protected CCIntervalAction(float d) {
		super(d);
	}

	public static CCIntervalAction action(float d){
		return new CCIntervalAction(d);
	}
	
    protected float _elapsed = 0f;
    private boolean _firstTick = true;
    
    @Override
    public CCIntervalAction copy() {
        return new CCIntervalAction(_duration);
    }

	@Override
	public boolean isDone() {
		return (_elapsed >= _duration);
	}

	@Override
	public void step(float dt) {
        if (_firstTick) {
            _firstTick = false;
        } else
            _elapsed += dt;
        update(Math.min(1, _elapsed / _duration));
	}

	@Override
	public void start(CCNode target) {
		super.start(target);
        _elapsed = 0.0f;
        _firstTick = true;
	}

	public CCIntervalAction reverse() {
		Log.w(LOG_TAG, "Override me");
		return null;
	}
	
}
